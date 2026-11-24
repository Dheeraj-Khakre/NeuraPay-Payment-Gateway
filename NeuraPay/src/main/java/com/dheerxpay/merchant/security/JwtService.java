package com.dheerxpay.merchant.security;

import com.dheerxpay.common.enums.UserRoles;
import com.dheerxpay.merchant.dto.response.AuthResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final Duration tokenLifetime;

    public JwtService(
            @Value("${security.jwt.secret:}") String base64Secret,
            @Value("${security.jwt.access-token-expiration:PT15M}") Duration tokenLifetime
    ) {
        String normalizedSecret = base64Secret.trim();
        if (normalizedSecret.isEmpty()) {
            throw new IllegalStateException(
                    "SECURITY_JWT_SECRET is required. Set it to a base64-encoded random key of at least 32 bytes."
            );
        }
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(normalizedSecret);
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException(
                    "SECURITY_JWT_SECRET must be valid base64. Generate one with: openssl rand -base64 32",
                    exception
            );
        }
        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    "SECURITY_JWT_SECRET must decode to at least 32 bytes. Generate one with: openssl rand -base64 32"
            );
        }
        if (tokenLifetime.isZero() || tokenLifetime.isNegative()) {
            throw new IllegalStateException("JWT access-token expiration must be positive");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.tokenLifetime = tokenLifetime;
    }

    public AuthResponse createAuthResponse(MerchantPrincipal principal) {
        Instant issuedAt = Instant.now();
        String token = Jwts.builder()
                .subject(principal.getUsername())
                .claim("userId", principal.userId().toString())
                .claim("merchantId", principal.merchantId().toString())
                .claim("role", principal.role().name())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(issuedAt.plus(tokenLifetime)))
                .signWith(signingKey)
                .compact();

        return new AuthResponse(
                token,
                "Bearer",
                tokenLifetime.toSeconds(),
                principal.userId(),
                principal.merchantId(),
                principal.email(),
                principal.role()
        );
    }

    public MerchantPrincipal parsePrincipal(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return new MerchantPrincipal(
                UUID.fromString(claims.get("userId", String.class)),
                UUID.fromString(claims.get("merchantId", String.class)),
                claims.getSubject(),
                "",
                UserRoles.valueOf(claims.get("role", String.class)),
                true
        );
    }
}
