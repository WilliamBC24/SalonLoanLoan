package service.sllbackend.utils;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class EncryptSSN {

    private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final SecretKey SECRET_KEY;
    static {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            SECRET_KEY = keyGen.generateKey();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate AES key", e);
        }
    }

    public static String encrypt(String ssn) throws Exception {
        byte[] iv = new byte[IV_LENGTH_BYTE];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY, spec);

        byte[] encrypted = cipher.doFinal(ssn.getBytes());

        byte[] encryptedWithIv = new byte[IV_LENGTH_BYTE + encrypted.length];
        System.arraycopy(iv, 0, encryptedWithIv, 0, IV_LENGTH_BYTE);
        System.arraycopy(encrypted, 0, encryptedWithIv, IV_LENGTH_BYTE, encrypted.length);

        return Base64.getEncoder().encodeToString(encryptedWithIv);
    }

    public static String decrypt(String encryptedSsn) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(encryptedSsn);

        byte[] iv = new byte[IV_LENGTH_BYTE];
        System.arraycopy(decoded, 0, iv, 0, IV_LENGTH_BYTE);

        byte[] ciphertext = new byte[decoded.length - IV_LENGTH_BYTE];
        System.arraycopy(decoded, IV_LENGTH_BYTE, ciphertext, 0, ciphertext.length);

        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY, spec);

        byte[] decrypted = cipher.doFinal(ciphertext);
        return new String(decrypted);
    }
}
