package com.example.FacebookLiteCode.security;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncryptedStringConverterTest {

    @BeforeAll
    static void initCrypto() {
        MessageCrypto.initFromSecret("test-secret");
    }

    @Test
    void convert_roundTrip_encryptsAndDecrypts() {
        EncryptedStringConverter converter = new EncryptedStringConverter();

        String plain = "hello world";
        String db = converter.convertToDatabaseColumn(plain);

        assertNotNull(db);
        assertNotEquals(plain, db);
        assertTrue(db.startsWith("ENC:"));

        String entity = converter.convertToEntityAttribute(db);
        assertEquals(plain, entity);
    }

    @Test
    void convert_backwardCompatibility_plaintextInDbIsReturnedAsIs() {
        EncryptedStringConverter converter = new EncryptedStringConverter();

        String plain = "legacy-plaintext";
        String entity = converter.convertToEntityAttribute(plain);

        assertEquals(plain, entity);
    }
}
