package com.stempo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDto {

    @NotBlank(message = "deviceTag is required")
    @Schema(description = "디바이스 식별자", example = "490154203237518", requiredMode = Schema.RequiredMode.REQUIRED)
    private String deviceTag;

    @Schema(description = "비밀번호", example = "password")
    private String password;
}
