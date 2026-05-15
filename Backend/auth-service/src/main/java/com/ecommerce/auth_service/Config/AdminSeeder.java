package com.ecommerce.auth_service.Config;

import com.ecommerce.auth_service.Domain.User;
import com.ecommerce.auth_service.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import reactor.core.publisher.Mono;

@Component
public class AdminSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedUser("adarsh@shopflux.com", "Adarsh123!", "Adarsh", "Kumar", "USER,ADMIN");
        seedUser("test@shopflux.com", "Test123!", "Test", "User", "USER");
    }

    private void seedUser(String email, String password, String firstName, String lastName, String roles) {
        userRepository.existsByEmail(email)
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.empty();
                    }

                    User user = new User();
                    user.setEmail(email);
                    user.setPasswordHash(passwordEncoder.encode(password));
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setRoles(roles);
                    user.setEnabled(true);
                    user.setLocked(false);
                    user.setFailedLoginAttempts(0);
                    user.setCreatedAt(Instant.now());
                    user.setUpdatedAt(Instant.now());

                    log.info("Seeding user: {}", email);
                    return userRepository.save(user);
                })
                .subscribe(
                        user -> log.info("User ready: email={}, roles={}", user.getEmail(), user.getRoles()),
                        error -> log.error("Failed to seed user: {}", email, error));
    }
}
