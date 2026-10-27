package com.example.lab6.server;

import com.example.lab6.common.AesKey;
import com.example.lab6.common.RsaKeys;
import com.example.lab6.common.RsaService;

import java.util.HashMap;
import java.util.Map;

public class KeyStorage {
    private static final Map<Integer, RsaKeys> rsaStore = new HashMap<>();
    private static final Map<String, AesKey> sessionStore = new HashMap<>();

    static {
        RsaService rsaService = new RsaService();
        for (int i = 1; i <= 10; i++) {
            rsaStore.put(i, rsaService.generateCryptoKeys());
        }
    }

    public static RsaKeys getRsa(int id) { return rsaStore.get(id); }
    public static int addRsa(RsaKeys keys) {
        int newId = rsaStore.size() + 1;
        rsaStore.put(newId, keys);
        return newId;
    }

    public static void saveSession(String sessionId, AesKey key) { sessionStore.put(sessionId, key); }
    public static AesKey getSession(String sessionId) { return sessionStore.get(sessionId); }
}