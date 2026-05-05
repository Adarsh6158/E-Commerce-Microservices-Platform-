package com.ecommerce.auth_service.Service;

import com.ecommerce.auth_service.Domain.RefreshToken;
import com.ecommerce.auth_service.Domain.User;
import com.ecommerce.auth_service.Repository.RefreshTokenRepository;
import com.ecommerce.auth_service.Repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecretKey jwtSecretKey;
    private final JwtParser jwtParser;
    private final ReactiveStringRedisTemplate redisTemplate;

    @Value("${auth.jwt.access-token-expiration-ms:3600000}")
    private long accessTokenExpirationMs;

    @Value("${auth.jwt.refresh-token-expiration-ms:604800000}")
    private long refreshTokenExpirationMs;

    public AuthService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       SecretKey jwtSecretKey,
                       JwtParser jwtParser,
                       ReactiveStringRedisTemplate redisTemplate) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtSecretKey = jwtSecretKey;
        this.jwtParser = jwtParser;
        this.redisTemplate = redisTemplate;
    }

    public Mono<Map<String, String>> register(String email, String password, String firstName, String lastName) {
        return userRepository.existsByEmail(email)
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("Email already registered"));
                    }

                    User user = new User();
                    user.setEmail(email);
                    user.setPasswordHash(passwordEncoder.encode(password));
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setRoles("USER");
                    user.setEnabled(true);
                    user.setLocked(false);
                    user.setFailedLoginAttempts(0);
                    user.setCreatedAt(Instant.now());
                    user.setUpdatedAt(Instant.now());

                    return userRepository.save(user);
                })
                .flatMap(this::generateTokenPair)
                .doOnSuccess(tokens -> log.info("User registered: email={}", email))
                .doOnError(e -> log.warn("Registration failed: email={}, reason={}", email, e.getMessage()));
    }

    public Mono<Map<String, String>> login(String email, String password) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid credentials")))
                .flatMap(user -> {

                    if (user.isLocked() && user.getLockExpiresAt() != null &&
                            user.getLockExpiresAt().isAfter(Instant.now())) {
                        return Mono.error(new IllegalStateException("Account locked. Try again later."));
                    }

                    if (user.isLocked() && user.getLockExpiresAt() != null &&
                            user.getLockExpiresAt().isBefore(Instant.now())) {
                        user.setLocked(false);
                        user.setFailedLoginAttempts(0);
                    }

                    if (!passwordEncoder.matches(password, user.getPasswordHash())) {
                        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);

                        if (user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
                            user.setLocked(true);
                            user.setLockExpiresAt(Instant.now().plus(LOCK_DURATION));
                            log.warn("Account locked due to failed attempts: email={}", email);
                        }

                        return userRepository.save(user)
                                .then(Mono.error(new IllegalArgumentException("Invalid credentials")));
                    }

                    if (!user.isEnabled()) {
                        return Mono.error(new IllegalStateException("Account disabled"));
                    }

                    user.setFailedLoginAttempts(0);
                    user.setLocked(false);
                    user.setUpdatedAt(Instant.now());

                    return userRepository.save(user);
                })
                .flatMap(this::generateTokenPair)
                .doOnSuccess(tokens -> log.info("User logged in: email={}", email));
    }

    public Mono<Map<String, String>> refresh(String refreshTokenValue) {
        return refreshTokenRepository.findByTokenAndRevokedFalse(refreshTokenValue)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid refresh token")))
                .flatMap(rt -> {
                    if (rt.getExpiresAt().isBefore(Instant.now())) {
                        return Mono.error(new IllegalArgumentException("Refresh token expired"));
                    }

                    rt.setRevoked(true);

                    return refreshTokenRepository.save(rt)
                            .then(userRepository.findById(rt.getUserId()));
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
                .flatMap(this::generateTokenPair);
    }

    public Mono<Void> logout(String userId, String accessToken) {
        return Mono.fromCallable(() -> {
                    Claims claims = jwtParser.parseSignedClaims(accessToken).getPayload();
                    long ttl = claims.getExpiration().getTime() - System.currentTimeMillis();
                    return Duration.ofMillis(Math.max(ttl, 0));
                })
                .flatMap(ttl ->
                        redisTemplate.opsForValue()
                                .set("blacklist:token:" + accessToken, "1", ttl)
                )
                .then(refreshTokenRepository.revokeAllByUserId(UUID.fromString(userId)))
                .then()
                .doOnSuccess(v -> log.info("User logged out: userId={}", userId));
    }

    public Mono<Boolean> isTokenBlacklisted(String token) {
        return redisTemplate.hasKey("blacklist:token:" + token);
    }

    private Mono<Map<String, String>> generateTokenPair(User user) {

        String accessToken = Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("roles", user.getRoles())
                .claim("firstName", user.getFirstName())
                .claim("lastName", user.getLastName())
                .claim("profileImageUrl", user.getProfileImageUrl())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .signWith(jwtSecretKey)
                .compact();

        String refreshTokenValue = UUID.randomUUID().toString();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(user.getId());
        refreshToken.setToken(refreshTokenValue);
        refreshToken.setExpiresAt(Instant.now().plusMillis(refreshTokenExpirationMs));
        refreshToken.setRevoked(false);
        refreshToken.setCreatedAt(Instant.now());

        return refreshTokenRepository.save(refreshToken)
                .thenReturn(Map.of(
                        "accessToken", accessToken,
                        "refreshToken", refreshTokenValue,
                        "tokenType", "Bearer",
                        "expiresIn", String.valueOf(accessTokenExpirationMs / 1000)
                ));
    }
}
