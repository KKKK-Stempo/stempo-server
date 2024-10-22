package com.stempo.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TotpAuthenticatorTest {

    private TotpAuthenticator totpAuthenticator;

    @BeforeEach
    void setUp() {
        totpAuthenticator = TotpAuthenticator.create("DEVICE_TAG", "SECRET_KEY");
    }

    @Test
    void TOTP가_정상적으로_생성되는지_확인한다() {
        // then
        assertThat(totpAuthenticator.getDeviceTag()).isEqualTo("DEVICE_TAG");
        assertThat(totpAuthenticator.getSecretKey()).isEqualTo("SECRET_KEY");
    }
}
