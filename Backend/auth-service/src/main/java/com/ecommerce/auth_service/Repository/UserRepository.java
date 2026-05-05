package com.ecommerce.auth_service.Repository;

import com.ecommerce.auth_service.Domain.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository extends R2dbcRepository<User, UUID> {

    Mono<User> findByEmail(String email);

    Mono<Boolean> existsByEmail(String email);
}
