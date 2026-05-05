package com.ecommerce.auth_service.Dto;

public record ProfileResponse (
    String id,
    String email,
    String firstName,
    String lastName,
    String profileImageUrl,
    String roles
) {}

