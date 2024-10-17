package com.stempo.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TotpAuthenticator {

    private String deviceTag;
    private String secretKey;

    public static TotpAuthenticator create(String memberId, String secretKey) {
        return TotpAuthenticator.builder()
                .deviceTag(memberId)
                .secretKey(secretKey)
                .build();
    }
}
