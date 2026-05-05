package com.ecommerce.auth_service.Dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest (
   @NotBlank String refreshToken
) {}

