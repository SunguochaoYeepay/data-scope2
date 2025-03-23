package com.datascope.infrastructure.security.encryption;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PasswordEncryptorTest {

    @Autowired
    private PasswordEncryptor passwordEncryptor;

    @Test
    void testEncryptAndDecrypt() {
        String password = "mySecretPassword";
        String salt = passwordEncryptor.generateSalt();

        String encryptedPassword = passwordEncryptor.encrypt(password, salt);
        assertNotNull(encryptedPassword);
        assertNotEquals(password, encryptedPassword);

        String decryptedPassword = passwordEncryptor.decrypt(encryptedPassword, salt);
        assertEquals(password, decryptedPassword);
    }

    @Test
    void testGenerateSalt() {
        String salt1 = passwordEncryptor.generateSalt();
        String salt2 = passwordEncryptor.generateSalt();

        assertNotNull(salt1);
        assertNotNull(salt2);
        assertNotEquals(salt1, salt2);
    }

    @Test
    void testDecryptWithInvalidSalt() {
        String password = "mySecretPassword";
        String salt = passwordEncryptor.generateSalt();
        String encryptedPassword = passwordEncryptor.encrypt(password, salt);

        String invalidSalt = passwordEncryptor.generateSalt();
        assertThrows(RuntimeException.class, () -> passwordEncryptor.decrypt(encryptedPassword, invalidSalt));
    }
}
