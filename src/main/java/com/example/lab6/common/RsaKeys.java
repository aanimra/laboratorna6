package com.example.lab6.common;

public class RsaKeys {
    private String publicKey;
    private String privateKey;

    public RsaKeys(String publicKey, String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public String getPublicKey() { return publicKey; }
    public String getPrivateKey() { return privateKey; }
}