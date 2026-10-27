package com.example.lab6.server;

import com.example.lab6.common.AesKey;
import com.example.lab6.common.RsaKeys;
import com.example.lab6.common.RsaService;
import com.example.lab6.common.AesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/crypto-keys")
public class CryptoController {

    private final RsaService rsaService = new RsaService();
    private final AesService aesService = new AesService();

    @PostMapping("/generate/rsa-keys")
    public ResponseEntity<?> generateRsa() {
        try {
            RsaKeys keys = rsaService.generateCryptoKeys();
            int id = KeyStorage.addRsa(keys);
            return ResponseEntity.ok(Map.of("id", id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error generating keys");
        }
    }

    @GetMapping("/rsa-public-key/{id}")
    public ResponseEntity<String> getPublicKey(@PathVariable int id) {
        RsaKeys keys = KeyStorage.getRsa(id);
        if (keys == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(keys.getPublicKey());
    }

    @PostMapping("/establish-session")
    public ResponseEntity<String> establishSession(
            @RequestHeader("x-rsa-id") int rsaId,
            @RequestHeader("x-session-id") String sessionId,
            @RequestBody Map<String, String> body) {
        try {
            RsaKeys rsaKeys = KeyStorage.getRsa(rsaId);
            if (rsaKeys == null) return ResponseEntity.badRequest().body("Invalid RSA ID");

            // Розшифровуємо AES ключ за допомогою RSA приватного ключа
            String encryptedAesData = body.get("encryptedData");
            String decryptedAesJson = rsaService.decrypt(rsaKeys.getPrivateKey(), encryptedAesData);

            // Парсимо key та iv (формат: key|iv)
            String[] parts = decryptedAesJson.split("\\|");
            AesKey aesKey = new AesKey(parts[0], parts[1]);
            
            KeyStorage.saveSession(sessionId, aesKey);
            return ResponseEntity.ok("Session established successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to establish session: " + e.getMessage());
        }
    }

    @PostMapping("/message")
    public ResponseEntity<String> handleMessage(
            @RequestHeader("x-session-id") String sessionId,
            @RequestBody Map<String, String> body) {
        try {
            AesKey aesKey = KeyStorage.getSession(sessionId);
            if (aesKey == null) return ResponseEntity.badRequest().body("Session not found");

            String encryptedMsg = body.get("message");
            String plainText = aesService.decrypt(aesKey, encryptedMsg);

            // Додаємо час отримання
            String responseText = plainText + " Received: " + LocalDateTime.now();

            // Шифруємо відповідь назад за допомогою AES
            String encryptedResponse = aesService.encrypt(aesKey, responseText);
            return ResponseEntity.ok(encryptedResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing message: " + e.getMessage());
        }
    }
}