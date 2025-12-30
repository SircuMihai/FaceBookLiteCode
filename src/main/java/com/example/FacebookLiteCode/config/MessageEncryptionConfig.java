package com.example.FacebookLiteCode.config;

import com.example.FacebookLiteCode.security.MessageCrypto;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageEncryptionConfig {

    @Value("${message.encryption.secret:}")
    private String messageEncryptionSecret;

    @Value("${jwt.secret:}")
    private String jwtSecret;

    @PostConstruct
    public void init() {
        String secret = (messageEncryptionSecret != null && !messageEncryptionSecret.trim().isEmpty())
                ? messageEncryptionSecret
                : jwtSecret;

        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalStateException("Missing encryption secret. Configure 'message.encryption.secret' (preferred) or 'jwt.secret'.");
        }

        MessageCrypto.initFromSecret(secret);
    }
}
