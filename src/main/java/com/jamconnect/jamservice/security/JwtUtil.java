package com.jamconnect.jamservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Jam Service never issues tokens — it only verifies ones issued by
 * User Service. This requires the SAME signing secret as User Service
 * (HS384, symmetric), shared via env var / Secrets Manager. No
 * asymmetric/JWKS setup for now, matching the "keep it simple" call
 * made earlier — revisit if more services start verifying tokens.
 */
@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey signingKey;

    private SecretKey getSigningKey() {
        if (signingKey == null) {
            signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        }
        return signingKey;
    }

    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.debug("JWT expired: {}", e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("JWT invalid: {}", e.getMessage());
        }
        return false;
    }

    public UUID extractUserId(String token) {
        String userId = parseClaims(token).get("userId", String.class);
        return UUID.fromString(userId);
    }

    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Roles arrive as a list of maps, e.g. [{"authority": "ROLE_LISTENER"}],
     * matching Spring Security's default GrantedAuthority JSON shape.
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Claims claims = parseClaims(token);
        List<Map<String, String>> rawRoles = claims.get("roles", List.class);
        if (rawRoles == null) {
            return List.of();
        }
        return rawRoles.stream()
                .map(role -> role.get("authority"))
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}