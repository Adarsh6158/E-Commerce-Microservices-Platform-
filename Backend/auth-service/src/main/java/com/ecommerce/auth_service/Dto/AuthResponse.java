package com.ecommerce.auth_service.Dto;

public record AuthResponse (
    String accessToken,
    String refreshToken,
    String tokenType,
    String expiresIn
) {}
