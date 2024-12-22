package com.stempo.config;

public interface EncryptionConfig {

    String getSecretKey();

    int getIvLengthBytes();

    int getGcmTagLengthBits();
}

