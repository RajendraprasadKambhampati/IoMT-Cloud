package com.iomt.crypto;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AESUtil - AES-256 Encryption/Decryption Utility
 * Provides symmetric encryption for medical data files
 * Uses AES/CBC/PKCS5Padding mode for security
 */
public class AESUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int KEY_SIZE = 128; // 128-bit for compatibility (256 needs JCE policy)
    private static final int IV_SIZE = 16;

    /**
     * Generate a new AES key
     */
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(KEY_SIZE, new SecureRandom());
        return keyGen.generateKey();
    }

    /**
     * Encrypt data using AES
     * Prepends IV to the ciphertext for decryption
     * @param data Plain data bytes
     * @param key AES secret key
     * @return Encrypted bytes (IV + ciphertext)
     */
    public static byte[] encrypt(byte[] data, SecretKey key) throws Exception {
        // Generate random IV
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        byte[] encrypted = cipher.doFinal(data);

        // Prepend IV to ciphertext
        byte[] result = new byte[IV_SIZE + encrypted.length];
        System.arraycopy(iv, 0, result, 0, IV_SIZE);
        System.arraycopy(encrypted, 0, result, IV_SIZE, encrypted.length);

        return result;
    }

    /**
     * Decrypt data using AES
     * Extracts IV from the first 16 bytes
     * @param encryptedData Encrypted bytes (IV + ciphertext)
     * @param key AES secret key
     * @return Decrypted plain data bytes
     */
    public static byte[] decrypt(byte[] encryptedData, SecretKey key) throws Exception {
        // Extract IV from first 16 bytes
        byte[] iv = new byte[IV_SIZE];
        System.arraycopy(encryptedData, 0, iv, 0, IV_SIZE);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Extract ciphertext
        byte[] ciphertext = new byte[encryptedData.length - IV_SIZE];
        System.arraycopy(encryptedData, IV_SIZE, ciphertext, 0, ciphertext.length);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        return cipher.doFinal(ciphertext);
    }

    /**
     * Convert SecretKey to Base64 string for storage
     */
    public static String keyToString(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * Convert Base64 string back to SecretKey
     */
    public static SecretKey stringToKey(String keyStr) {
        byte[] decoded = Base64.getDecoder().decode(keyStr);
        return new SecretKeySpec(decoded, ALGORITHM);
    }

    /**
     * Calculate SHA-256 hash of data (for file integrity verification)
     */
    public static String sha256Hash(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data);
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Hash a password using SHA-256
     */
    public static String hashPassword(String password) {
        try {
            return sha256Hash(password.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }
}
