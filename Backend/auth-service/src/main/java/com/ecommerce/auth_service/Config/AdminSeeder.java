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
        userRepository.existsByEmail("admin@shophub.com")
                .flatMap(exists -> {
                    if (exists) {
                        log.info("Admin user already exists, skipping seed");
                        return userRepository.findByEmail("admin@shophub.com");
                    }

                    User admin = new User();
                    admin.setEmail("admin@shophub.com");
                    admin.setPasswordHash(passwordEncoder.encode("Admin123!"));
                    admin.setFirstName("Admin");
                    admin.setLastName("ShopHub");
                    admin.setRoles("USER,ADMIN");
                    admin.setEnabled(true);
                    admin.setLocked(false);
                    admin.setFailedLoginAttempts(0);
                    admin.setCreatedAt(Instant.now());
                    admin.setUpdatedAt(Instant.now());

                    log.info("Seeding admin user: admin@shophub.com");
                    return userRepository.save(admin);
                })
                .subscribe(
                        user -> log.info("Admin user ready: email={}, roles={}", user.getEmail(), user.getRoles()),
                        error -> log.error("Failed to seed admin user", error)
                );
    }
}
