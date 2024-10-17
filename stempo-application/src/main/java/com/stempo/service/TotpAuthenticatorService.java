package com.stempo.service;

public interface TotpAuthenticatorService {

    String resetAuthenticator(String deviceTag);

    boolean isAuthenticatorValid(String deviceTag, String totp);

    boolean isAuthenticatorExist(String deviceTag);

    String generateSecretKey(String deviceTag);
}
