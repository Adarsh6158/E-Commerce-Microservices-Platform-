package com.ecommerce.auth_service.Controller;

import com.ecommerce.auth_service.Dto.AuthResponse;
import com.ecommerce.auth_service.Dto.LoginRequest;
import com.ecommerce.auth_service.Dto.RefreshRequest;
import com.ecommerce.auth_service.Dto.RegisterRequest;
import com.ecommerce.auth_service.Service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request.email(), request.password(), request.firstName(), request.lastName())
                .map(m -> new AuthResponse(
                        m.get("accessToken"),
                        m.get("refreshToken"),
                        m.get("tokenType"),
                        m.get("expiresIn")
                ));
    }

    @PostMapping("/login")
    public Mono<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request.email(), request.password())
                .map(m -> new AuthResponse(
                        m.get("accessToken"),
                        m.get("refreshToken"),
                        m.get("tokenType"),
                        m.get("expiresIn")
                ));
    }

    @PostMapping("/refresh")
    public Mono<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request.refreshToken())
                .map(m -> new AuthResponse(
                        m.get("accessToken"),
                        m.get("refreshToken"),
                        m.get("tokenType"),
                        m.get("expiresIn")
                ));
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> logout(@RequestHeader("X-User-Id") String userId,
                             @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        return authService.logout(userId, token);
    }

    @GetMapping("/validate")
    public Mono<Map<String, Object>> validate(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return authService.isTokenBlacklisted(token)
                .map(blacklisted -> Map.<String, Object>of("valid", !blacklisted));
    }
}
