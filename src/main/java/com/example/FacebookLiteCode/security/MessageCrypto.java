package com.example.FacebookLiteCode.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public final class MessageCrypto {
    private static final String PREFIX = "ENC:";
    private static final String CIPHER_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final int IV_LENGTH_BYTES = 12;

    private static volatile SecretKey secretKey;
    private static final SecureRandom secureRandom = new SecureRandom();

    private MessageCrypto() {
    }

    public static void initFromSecret(String secret) {
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalArgumentException("Encryption secret must not be blank");
        }
        secretKey = new SecretKeySpec(sha256(secret), "AES");
    }

    public static String encrypt(String plainText) {
        if (plainText == null) {
            return null;
        }
        if (plainText.startsWith(PREFIX)) {
            return plainText;
        }

        SecretKey key = requireKey();

        try {
            byte[] iv = new byte[IV_LENGTH_BYTES];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));

            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            byte[] payload = new byte[iv.length + cipherText.length];

            System.arraycopy(iv, 0, payload, 0, iv.length);
            System.arraycopy(cipherText, 0, payload, iv.length, cipherText.length);

            return PREFIX + Base64.getEncoder().encodeToString(payload);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to encrypt message", e);
        }
    }

    public static String decrypt(String storedValue) {
        if (storedValue == null) {
            return null;
        }
        if (!storedValue.startsWith(PREFIX)) {
            return storedValue;
        }

        SecretKey key = requireKey();

        try {
            String base64 = storedValue.substring(PREFIX.length());
            byte[] payload = Base64.getDecoder().decode(base64);
            if (payload.length < IV_LENGTH_BYTES + 1) {
                return storedValue;
            }

            byte[] iv = new byte[IV_LENGTH_BYTES];
            byte[] cipherText = new byte[payload.length - IV_LENGTH_BYTES];

            System.arraycopy(payload, 0, iv, 0, IV_LENGTH_BYTES);
            System.arraycopy(payload, IV_LENGTH_BYTES, cipherText, 0, cipherText.length);

            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));

            byte[] plain = cipher.doFinal(cipherText);
            return new String(plain, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return storedValue;
        }
    }

    private static SecretKey requireKey() {
        SecretKey key = secretKey;
        if (key == null) {
            throw new IllegalStateException("MessageCrypto not initialized. Ensure MessageEncryptionConfig ran and a secret is configured.");
        }
        return key;
    }

    private static byte[] sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(input.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to derive encryption key", e);
        }
    }
}
