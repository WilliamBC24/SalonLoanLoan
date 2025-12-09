package service.sllbackend.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncryptSSNTest {

    @Test
    void testEncrypt_ShouldReturnNonNullEncryptedString() throws Exception {
        // Arrange
        String ssn = "123-45-6789";

        // Act
        String encrypted = EncryptSSN.encrypt(ssn);

        // Assert
        assertNotNull(encrypted);
        assertFalse(encrypted.isEmpty());
        assertNotEquals(ssn, encrypted);
    }

    @Test
    void testEncryptDecrypt_ShouldReturnOriginalValue() throws Exception {
        // Arrange
        String originalSsn = "123-45-6789";

        // Act
        String encrypted = EncryptSSN.encrypt(originalSsn);
        String decrypted = EncryptSSN.decrypt(encrypted);

        // Assert
        assertEquals(originalSsn, decrypted);
    }

    @Test
    void testEncrypt_SameValueTwice_ShouldProduceDifferentEncryption() throws Exception {
        // Arrange
        String ssn = "123-45-6789";

        // Act
        String encrypted1 = EncryptSSN.encrypt(ssn);
        String encrypted2 = EncryptSSN.encrypt(ssn);

        // Assert - Due to random IV, same plaintext should produce different ciphertext
        assertNotEquals(encrypted1, encrypted2);
        
        // But both should decrypt to the same original value
        assertEquals(ssn, EncryptSSN.decrypt(encrypted1));
        assertEquals(ssn, EncryptSSN.decrypt(encrypted2));
    }

    @Test
    void testEncryptDecrypt_WithEmptyString_ShouldWork() throws Exception {
        // Arrange
        String emptyString = "";

        // Act
        String encrypted = EncryptSSN.encrypt(emptyString);
        String decrypted = EncryptSSN.decrypt(encrypted);

        // Assert
        assertEquals(emptyString, decrypted);
    }

    @Test
    void testEncryptDecrypt_WithSpecialCharacters_ShouldWork() throws Exception {
        // Arrange
        String ssnWithSpecialChars = "123-45-6789!@#$%";

        // Act
        String encrypted = EncryptSSN.encrypt(ssnWithSpecialChars);
        String decrypted = EncryptSSN.decrypt(encrypted);

        // Assert
        assertEquals(ssnWithSpecialChars, decrypted);
    }

    @Test
    void testDecrypt_WithInvalidData_ShouldThrowException() {
        // Arrange
        String invalidEncryptedData = "not-a-valid-encrypted-string";

        // Act & Assert
        assertThrows(Exception.class, () -> EncryptSSN.decrypt(invalidEncryptedData));
    }
}
