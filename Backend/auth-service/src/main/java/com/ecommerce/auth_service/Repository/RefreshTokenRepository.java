package com.ecommerce.auth_service.Repository;

import com.ecommerce.auth_service.Domain.RefreshToken;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RefreshTokenRepository extends R2dbcRepository<RefreshToken, UUID> {

    Mono<RefreshToken> findByTokenAndRevokedFalse(String token);

    @Modifying
    @Query("UPDATE refresh_tokens SET revoked = true WHERE user_id = :userId AND revoked = false")
    Mono<Integer> revokeAllByUserId(UUID userId);
}