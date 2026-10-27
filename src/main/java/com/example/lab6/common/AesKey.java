package com.example.lab6.common;

public class AesKey {
    private String key;
    private String iv;

    public AesKey(String key, String iv) {
        this.key = key;
        this.iv = iv;
    }

    public String getKey() { return key; }
    public String getIv() { return iv; }
}