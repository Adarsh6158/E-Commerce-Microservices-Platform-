package com.ecommerce.auth_service.Controller;

import com.ecommerce.auth_service.Dto.ProfileResponse;
import com.ecommerce.auth_service.Dto.ProfileUpdateRequest;
import com.ecommerce.auth_service.Repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/auth/profile")
public class ProfileController {

    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public Mono<ProfileResponse> getProfile(@RequestHeader("X-User-Id") String userId) {
        return userRepository.findById(UUID.fromString(userId))
                .map(user -> new ProfileResponse(
                        user.getId().toString(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getProfileImageUrl(),
                        user.getRoles()
                ))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")));
    }

    @PutMapping
    public Mono<ProfileResponse> updateProfile(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody ProfileUpdateRequest request) {

        return userRepository.findById(UUID.fromString(userId))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
                .flatMap(user -> {

                    if (request.firstName() != null && !request.firstName().isBlank()) {
                        user.setFirstName(request.firstName());
                    }

                    if (request.lastName() != null && !request.lastName().isBlank()) {
                        user.setLastName(request.lastName());
                    }

                    if (request.profileImageUrl() != null) {
                        // Limit image data to 500KB for base64 data URLs
                        if (request.profileImageUrl().length() > 512000) {
                            return Mono.error(new IllegalArgumentException("Image too large. Maximum 500KB."));
                        }
                        user.setProfileImageUrl(request.profileImageUrl());
                    }

                    user.setUpdatedAt(Instant.now());
                    return userRepository.save(user);
                })
                .map(user -> new ProfileResponse(
                        user.getId().toString(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getProfileImageUrl(),
                        user.getRoles()
                ));
    }
}
