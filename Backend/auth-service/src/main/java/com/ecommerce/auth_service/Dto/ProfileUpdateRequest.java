package com.ecommerce.auth_service.Dto;

public record ProfileUpdateRequest (
    String firstName,
    String lastName,
    String profileImageUrl
) {}

