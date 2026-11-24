package com.dheerxpay.merchant.security;

import com.dheerxpay.common.enums.UserRoles;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService(
            Base64.getEncoder().encodeToString(new byte[32]),
            Duration.ofMinutes(15)
    );

    @Test
    void tokenContainsAuthenticatedUserAndMerchantIdentity() {
        UUID userId = UUID.randomUUID();
        UUID merchantId = UUID.randomUUID();
        MerchantPrincipal original = new MerchantPrincipal(
                userId, merchantId, "merchant@example.com", "", UserRoles.OWNER, true
        );

        MerchantPrincipal parsed = jwtService.parsePrincipal(
                jwtService.createAuthResponse(original).accessToken()
        );

        assertEquals(userId, parsed.userId());
        assertEquals(merchantId, parsed.merchantId());
        assertEquals(original.email(), parsed.email());
        assertEquals(UserRoles.OWNER, parsed.role());
    }

    @Test
    void invalidTokenIsRejected() {
        assertThrows(RuntimeException.class, () -> jwtService.parsePrincipal("not-a-jwt"));
    }

    @Test
    void jwtSecretMayHaveSurroundingWhitespace() {
        String secret = " \r\n" + Base64.getEncoder().encodeToString(new byte[32]) + "\t ";
        UUID merchantId = UUID.randomUUID();

        JwtService service = new JwtService(secret, Duration.ofMinutes(15));

        assertEquals(merchantId, service.parsePrincipal(service.createAuthResponse(
                new MerchantPrincipal(
                        UUID.randomUUID(), merchantId, "merchant@example.com", "", UserRoles.OWNER, true
                )
        ).accessToken()).merchantId());
    }
}
