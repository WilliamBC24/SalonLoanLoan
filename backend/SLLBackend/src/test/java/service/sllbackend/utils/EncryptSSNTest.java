package service.sllbackend.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncryptSSNTest {

    @Test
    void testEncryptAndDecrypt_ShouldReturnOriginalValue() throws Exception {
        // Arrange
        String originalSSN = "123-45-6789";

        // Act
        String encrypted = EncryptSSN.encrypt(originalSSN);
        String decrypted = EncryptSSN.decrypt(encrypted);

        // Assert
        assertNotNull(encrypted);
        assertNotEquals(originalSSN, encrypted);
        assertEquals(originalSSN, decrypted);
    }

    @Test
    void testEncrypt_DifferentInvocations_ShouldProduceDifferentCiphertext() throws Exception {
        // Arrange
        String ssn = "987-65-4321";

        // Act
        String encrypted1 = EncryptSSN.encrypt(ssn);
        String encrypted2 = EncryptSSN.encrypt(ssn);

        // Assert
        assertNotEquals(encrypted1, encrypted2, "Same plaintext should produce different ciphertext due to random IV");
    }

    @Test
    void testDecrypt_WithValidEncryptedData_ShouldSucceed() throws Exception {
        // Arrange
        String originalSSN = "555-12-3456";
        String encrypted = EncryptSSN.encrypt(originalSSN);

        // Act
        String decrypted = EncryptSSN.decrypt(encrypted);

        // Assert
        assertEquals(originalSSN, decrypted);
    }
}
