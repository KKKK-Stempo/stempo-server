package com.stempo.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.stempo.entity.TotpAuthenticatorEntity;
import com.stempo.model.TotpAuthenticator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TotpAuthenticatorMapperTest {

    private TotpAuthenticatorMapper totpAuthenticatorMapper;

    @BeforeEach
    void setUp() {
        totpAuthenticatorMapper = new TotpAuthenticatorMapper();
    }

    @Test
    void 도메인을_엔티티로_매핑한다() {
        // given
        TotpAuthenticator totpAuthenticator = TotpAuthenticator.builder()
                .deviceTag("device123")
                .secretKey("secretKey123")
                .build();

        // when
        TotpAuthenticatorEntity entity = totpAuthenticatorMapper.toEntity(totpAuthenticator);

        // then
        assertThat(entity.getDeviceTag()).isEqualTo(totpAuthenticator.getDeviceTag());
        assertThat(entity.getSecretKey()).isEqualTo(totpAuthenticator.getSecretKey());
    }

    @Test
    void 엔티티를_도메인으로_매핑한다() {
        // given
        TotpAuthenticatorEntity entity = TotpAuthenticatorEntity.builder()
                .deviceTag("device123")
                .secretKey("secretKey123")
                .build();

        // when
        TotpAuthenticator totpAuthenticator = totpAuthenticatorMapper.toDomain(entity);

        // then
        assertThat(totpAuthenticator.getDeviceTag()).isEqualTo(entity.getDeviceTag());
        assertThat(totpAuthenticator.getSecretKey()).isEqualTo(entity.getSecretKey());
    }
}
