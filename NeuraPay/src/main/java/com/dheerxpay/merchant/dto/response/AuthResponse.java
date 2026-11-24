package com.dheerxpay.merchant.dto.response;

import com.dheerxpay.common.enums.UserRoles;

import java.util.UUID;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        UUID userId,
        UUID merchantId,
        String email,
        UserRoles role
) {
}
