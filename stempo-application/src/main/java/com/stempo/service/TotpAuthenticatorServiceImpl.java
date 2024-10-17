package com.stempo.service;

import com.stempo.model.TotpAuthenticator;
import com.stempo.repository.TotpAuthenticatorRepository;
import com.stempo.util.EncryptionUtils;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TotpAuthenticatorServiceImpl implements TotpAuthenticatorService {

    private final TotpAuthenticatorRepository repository;
    private final GoogleAuthenticator googleAuthenticator;
    private final EncryptionUtils encryptionUtils;

    @Override
    @Transactional
    public String resetAuthenticator(String deviceTag) {
        TotpAuthenticator authenticator = repository.getById(deviceTag);
        repository.delete(authenticator);
        return deviceTag;
    }

    @Override
    @Transactional
    public String generateSecretKey(String deviceTag) {
        GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();
        String secretKey = key.getKey();
        saveAuthenticator(deviceTag, secretKey);
        return secretKey;
    }

    @Override
    public boolean isAuthenticatorValid(String deviceTag, String totp) {
        Optional<TotpAuthenticator> authenticatorOpt = repository.findById(deviceTag);
        return authenticatorOpt.isPresent() && validateTotp(authenticatorOpt.get(), totp);
    }

    private boolean validateTotp(TotpAuthenticator authenticator, String totp) {
        String secretKey = encryptionUtils.decrypt(authenticator.getSecretKey());
        return googleAuthenticator.authorize(secretKey, Integer.parseInt(totp));
    }

    private void saveAuthenticator(String deviceTag, String secretKey) {
        String encryptedSecretKey = encryptionUtils.encrypt(secretKey);
        TotpAuthenticator authenticator = TotpAuthenticator.create(deviceTag, encryptedSecretKey);
        repository.save(authenticator);
    }

    @Override
    public boolean isAuthenticatorExist(String deviceTag) {
        return repository.existsById(deviceTag);
    }
}
