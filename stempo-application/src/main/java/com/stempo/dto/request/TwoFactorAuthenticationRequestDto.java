package com.stempo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TwoFactorAuthenticationRequestDto {

    @NotNull(message = "Device Tag is required")
    @Schema(description = "디바이스 식별자", example = "490154203237518", requiredMode = Schema.RequiredMode.REQUIRED)
    private String deviceTag;

    @NotNull(message = "TOTP is required")
    @Schema(description = "TOTP", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String totp;
}
