package com.stempo.mapper;

import com.stempo.entity.TotpAuthenticatorEntity;
import com.stempo.model.TotpAuthenticator;
import org.springframework.stereotype.Component;

@Component
public class TotpAuthenticatorMapper {

    public TotpAuthenticatorEntity toEntity(TotpAuthenticator totpAuthenticator) {
        return TotpAuthenticatorEntity.builder()
                .deviceTag(totpAuthenticator.getDeviceTag())
                .secretKey(totpAuthenticator.getSecretKey())
                .build();
    }

    public TotpAuthenticator toDomain(TotpAuthenticatorEntity entity) {
        return TotpAuthenticator.builder()
                .deviceTag(entity.getDeviceTag())
                .secretKey(entity.getSecretKey())
                .build();
    }
}
