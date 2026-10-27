package com.example.lab6.client;

import com.example.lab6.common.AesKey;
import com.example.lab6.common.AesService;
import com.example.lab6.common.RsaService;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class ConsoleClient {
    private static final String BASE_URL = "http://localhost:8080/api/crypto-keys";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        RestTemplate restTemplate = new RestTemplate();
        RsaService rsaService = new RsaService();
        AesService aesService = new AesService();

        System.out.println("=== Secure Communication Client Started ===");

        while (true) {
            System.out.print("\nEnter message to send (or 'exit' to quit): ");
            String message = scanner.nextLine();
            if ("exit".equalsIgnoreCase(message)) break;

            try {
                int rsaId = 1; // Беремо першу пару ключів згенерованих сервером
                
                // а. Отримати публічний ключ з серверу
                ResponseEntity<String> pubKeyResponse = restTemplate.getForEntity(
                        BASE_URL + "/rsa-public-key/" + rsaId, String.class);
                String publicKey = pubKeyResponse.getBody();

                // b, c. Створити таємний ключ AES та ідентифікатор сесії
                AesKey aesKey = aesService.generateSecretKey();
                String sessionId = UUID.randomUUID().toString();

                // d. Зашифрувати публічним ключем інформацію для симетричного каналу
                String aesDataToEncrypt = aesKey.getKey() + "|" + aesKey.getIv();
                String encryptedAesData = rsaService.encrypt(publicKey, aesDataToEncrypt);

                // Відправляємо запит на встановлення сесії
                HttpHeaders headers = new HttpHeaders();
                headers.set("x-rsa-id", String.valueOf(rsaId));
                headers.set("x-session-id", sessionId);
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Map<String, String>> sessionEntity = new HttpEntity<>(
                        Map.of("encryptedData", encryptedAesData), headers);
                
                restTemplate.postForEntity(BASE_URL + "/establish-session", sessionEntity, String.class);
                System.out.println("[Client] Session established with ID: " + sessionId);

                // f. Шифрування повідомлення юзера за допомогою AES
                String encryptedMessage = aesService.encrypt(aesKey, message);

                HttpHeaders msgHeaders = new HttpHeaders();
                msgHeaders.set("x-session-id", sessionId);
                msgHeaders.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Map<String, String>> msgEntity = new HttpEntity<>(
                        Map.of("message", encryptedMessage), msgHeaders);

                // g. Відправка до серверу та отримання відповіді
                ResponseEntity<String> response = restTemplate.postForEntity(
                        BASE_URL + "/message", msgEntity, String.class);

                // h. Дешифрування відповіді та вивід на екран
                String decryptedResponse = aesService.decrypt(aesKey, response.getBody());
                System.out.println("[Server Response]: " + decryptedResponse);

            } catch (Exception e) {
                System.out.println("[Error]: " + e.getMessage());
            }
        }
        scanner.close();
    }
}