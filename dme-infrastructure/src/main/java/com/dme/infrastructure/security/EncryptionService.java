package com.dme.infrastructure.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
@Slf4j
public class EncryptionService {

    private static final String ALGORITHM = "AES";
    private final SecretKey secretKey;

    public EncryptionService() throws Exception {
        // Initialize a 256-bit key
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(256);
        this.secretKey = keyGenerator.generateKey();
    }

    public String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decrypt(String encryptedText) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }

    public String hashPassword(String password) {
        // In production, use bcrypt instead
        return org.springframework.security.crypto.bcrypt.BCrypt.hashpw(password, org.springframework.security.crypto.bcrypt.BCrypt.gensalt());
    }

    public boolean verifyPassword(String password, String hash) {
        return org.springframework.security.crypto.bcrypt.BCrypt.checkpw(password, hash);
    }
}
