package com.example.FacebookLiteCode.security;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory store that keeps track of the JWTs issued by the application.
 * This allows us to invalidate tokens on logout and to ensure that only
 * server-issued tokens are accepted by the authentication filter.
 * Now supports both access tokens and refresh tokens.
 */
@Component
public class JwtTokenStore {

    private static final class StoredToken {
        private final int userId;
        private final String username;
        private final Instant issuedAt;
        private final String tokenType; // "access" or "refresh"

        private StoredToken(int userId, String username, Instant issuedAt, String tokenType) {
            this.userId = userId;
            this.username = username;
            this.issuedAt = issuedAt;
            this.tokenType = tokenType;
        }
    }

    private final Map<String, StoredToken> activeTokens = new ConcurrentHashMap<>();
    private final Map<String, String> refreshToAccessMap = new ConcurrentHashMap<>(); // refresh token -> access token mapping

    public void storeToken(String token, int userId, String username, String tokenType) {
        if (token == null || username == null || tokenType == null) {
            return;
        }
        activeTokens.put(token, new StoredToken(userId, username, Instant.now(), tokenType));
    }

    public void linkRefreshToAccess(String refreshToken, String accessToken) {
        if (refreshToken != null && accessToken != null) {
            refreshToAccessMap.put(refreshToken, accessToken);
        }
    }

    public boolean isTokenActive(String token, String username) {
        if (token == null || username == null) {
            return false;
        }
        StoredToken storedToken = activeTokens.get(token);
        return storedToken != null && username.equals(storedToken.username);
    }

    public Optional<Integer> getUserIdByToken(String token) {
        StoredToken storedToken = activeTokens.get(token);
        return storedToken == null ? Optional.empty() : Optional.of(storedToken.userId);
    }

    public String getTokenType(String token) {
        StoredToken storedToken = activeTokens.get(token);
        return storedToken == null ? null : storedToken.tokenType;
    }

    public void revokeToken(String token) {
        if (token == null) {
            return;
        }
        StoredToken storedToken = activeTokens.get(token);
        if (storedToken != null && "refresh".equals(storedToken.tokenType)) {
            // If revoking refresh token, also revoke linked access token
            refreshToAccessMap.entrySet().removeIf(entry -> entry.getKey().equals(token));
            String linkedAccessToken = refreshToAccessMap.remove(token);
            if (linkedAccessToken != null) {
                activeTokens.remove(linkedAccessToken);
            }
        }
        activeTokens.remove(token);
        refreshToAccessMap.values().removeIf(accessToken -> accessToken.equals(token));
    }

    public void revokeTokensByUsername(String username) {
        if (username == null) {
            return;
        }
        // Remove all tokens for this user
        activeTokens.entrySet().removeIf(entry -> username.equals(entry.getValue().username));
        // Clean up refresh token mappings
        refreshToAccessMap.clear();
    }

    public void revokeRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            return;
        }
        // Revoke the refresh token and its linked access token
        String linkedAccessToken = refreshToAccessMap.remove(refreshToken);
        if (linkedAccessToken != null) {
            activeTokens.remove(linkedAccessToken);
        }
        activeTokens.remove(refreshToken);
    }
}

