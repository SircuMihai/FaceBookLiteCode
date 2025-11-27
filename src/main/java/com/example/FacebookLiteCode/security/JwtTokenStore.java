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
 */
@Component
public class JwtTokenStore {

    private static final class StoredToken {
        private final int userId;
        private final String username;
        private final Instant issuedAt;

        private StoredToken(int userId, String username, Instant issuedAt) {
            this.userId = userId;
            this.username = username;
            this.issuedAt = issuedAt;
        }
    }

    private final Map<String, StoredToken> activeTokens = new ConcurrentHashMap<>();

    public void storeToken(String token, int userId, String username) {
        if (token == null || username == null) {
            return;
        }
        activeTokens.put(token, new StoredToken(userId, username, Instant.now()));
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

    public void revokeToken(String token) {
        if (token == null) {
            return;
        }
        activeTokens.remove(token);
    }

    public void revokeTokensByUsername(String username) {
        if (username == null) {
            return;
        }
        activeTokens.entrySet().removeIf(entry -> username.equals(entry.getValue().username));
    }
}

