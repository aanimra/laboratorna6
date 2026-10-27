package com.example.lab6;

import com.example.lab6.common.AesKey;
import com.example.lab6.common.AesService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AesServiceTest {

    @Test
    public void testAesEncryption() {
        AesService service = new AesService();
        AesKey key = service.generateSecretKey();
        String original = "Hello Java";
        String encrypted = service.encrypt(key, original);
        String decrypted = service.decrypt(key, encrypted);
        assertEquals(original, decrypted);
    }
}