package com.stempo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.stempo.model.TotpAuthenticator;
import com.stempo.repository.TotpAuthenticatorRepository;
import com.stempo.util.EncryptionUtils;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TotpAuthenticatorServiceImplTest {

    @Mock
    private TotpAuthenticatorRepository repository;

    @Mock
    private GoogleAuthenticator googleAuthenticator;

    @Mock
    private EncryptionUtils encryptionUtils;

    @InjectMocks
    private TotpAuthenticatorServiceImpl totpAuthenticatorService;

    @Test
    void 비밀키_재설정시_디바이스태그를_반환한다() {
        // given
        String deviceTag = "test-device-tag";
        TotpAuthenticator authenticator = TotpAuthenticator.create(deviceTag, "encrypted-secret-key");

        when(repository.getById(deviceTag)).thenReturn(authenticator);

        // when
        String result = totpAuthenticatorService.resetAuthenticator(deviceTag);

        // then
        assertThat(result).isEqualTo(deviceTag);
        verify(repository).getById(deviceTag);
        verify(repository).delete(authenticator);
    }

    @Test
    void 비밀키_생성시_새로운_비밀키를_반환한다() {
        // given
        String deviceTag = "test-device-tag";
        String secretKey = "secret-key";
        String encryptedSecretKey = "encrypted-secret-key";

        GoogleAuthenticatorKey googleAuthenticatorKey = new GoogleAuthenticatorKey.Builder(secretKey).build();

        when(googleAuthenticator.createCredentials()).thenReturn(googleAuthenticatorKey);
        when(encryptionUtils.encrypt(secretKey)).thenReturn(encryptedSecretKey);

        // when
        String result = totpAuthenticatorService.generateSecretKey(deviceTag);

        // then
        assertThat(result).isEqualTo(secretKey);
        verify(googleAuthenticator).createCredentials();
        verify(encryptionUtils).encrypt(secretKey);
        verify(repository).save(any(TotpAuthenticator.class));
    }

    @Test
    void 유효한_비밀키로_인증에_성공한다() {
        // given
        String deviceTag = "test-device-tag";
        String totp = "123456";
        String encryptedSecretKey = "encrypted-secret-key";
        String decryptedSecretKey = "decrypted-secret-key";
        TotpAuthenticator authenticator = TotpAuthenticator.create(deviceTag, encryptedSecretKey);

        when(repository.findById(deviceTag)).thenReturn(Optional.of(authenticator));
        when(encryptionUtils.decrypt(encryptedSecretKey)).thenReturn(decryptedSecretKey);
        when(googleAuthenticator.authorize(decryptedSecretKey, Integer.parseInt(totp))).thenReturn(true);

        // when
        boolean isValid = totpAuthenticatorService.isAuthenticatorValid(deviceTag, totp);

        // then
        assertThat(isValid).isTrue();
        verify(repository).findById(deviceTag);
        verify(encryptionUtils).decrypt(encryptedSecretKey);
        verify(googleAuthenticator).authorize(decryptedSecretKey, Integer.parseInt(totp));
    }

    @Test
    void 잘못된_비밀키로_인증에_실패한다() {
        // given
        String deviceTag = "test-device-tag";
        String totp = "123456";
        String encryptedSecretKey = "encrypted-secret-key";
        String decryptedSecretKey = "decrypted-secret-key";
        TotpAuthenticator authenticator = TotpAuthenticator.create(deviceTag, encryptedSecretKey);

        when(repository.findById(deviceTag)).thenReturn(Optional.of(authenticator));
        when(encryptionUtils.decrypt(encryptedSecretKey)).thenReturn(decryptedSecretKey);
        when(googleAuthenticator.authorize(decryptedSecretKey, Integer.parseInt(totp))).thenReturn(false);

        // when
        boolean isValid = totpAuthenticatorService.isAuthenticatorValid(deviceTag, totp);

        // then
        assertThat(isValid).isFalse();
        verify(repository).findById(deviceTag);
        verify(encryptionUtils).decrypt(encryptedSecretKey);
        verify(googleAuthenticator).authorize(decryptedSecretKey, Integer.parseInt(totp));
    }

    @Test
    void 비밀키가_없을_때_인증에_실패한다() {
        // given
        String deviceTag = "test-device-tag";
        String totp = "123456";

        when(repository.findById(deviceTag)).thenReturn(Optional.empty());

        // when
        boolean isValid = totpAuthenticatorService.isAuthenticatorValid(deviceTag, totp);

        // then
        assertThat(isValid).isFalse();
        verify(repository).findById(deviceTag);
        verifyNoMoreInteractions(encryptionUtils, googleAuthenticator);
    }

    @Test
    void 비밀키가_존재하면_true를_반환한다() {
        // given
        String deviceTag = "test-device-tag";

        when(repository.existsById(deviceTag)).thenReturn(true);

        // when
        boolean exists = totpAuthenticatorService.isAuthenticatorExist(deviceTag);

        // then
        assertThat(exists).isTrue();
        verify(repository).existsById(deviceTag);
    }

    @Test
    void 비밀키가_존재하지_않으면_false를_반환한다() {
        // given
        String deviceTag = "test-device-tag";

        when(repository.existsById(deviceTag)).thenReturn(false);

        // when
        boolean exists = totpAuthenticatorService.isAuthenticatorExist(deviceTag);

        // then
        assertThat(exists).isFalse();
        verify(repository).existsById(deviceTag);
    }
}
