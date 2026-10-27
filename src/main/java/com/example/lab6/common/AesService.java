package com.example.lab6.common;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AesService {

    public AesKey generateSecretKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            SecretKey secretKey = keyGen.generateKey();
            
            byte[] iv = new byte[16];
            new java.security.SecureRandom().nextBytes(iv);

            String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
            String encodedIv = Base64.getEncoder().encodeToString(iv);

            return new AesKey(encodedKey, encodedIv);
        } catch (Exception e) {
            throw new RuntimeException("Error generating AES key", e);
        }
    }

    public String encrypt(AesKey aesKey, String plainText) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(aesKey.getKey());
            byte[] ivBytes = Base64.getDecoder().decode(aesKey.getIv());

            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting with AES", e);
        }
    }

    public String decrypt(AesKey aesKey, String cipherText) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(aesKey.getKey());
            byte[] ivBytes = Base64.getDecoder().decode(aesKey.getIv());

            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            byte[] decoded = Base64.getDecoder().decode(cipherText);
            return new String(cipher.doFinal(decoded), "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting with AES", e);
        }
    }
}