package com.stempo.api.domain.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {

    @NotBlank
    @Schema(description = "아이디", example = "test", minLength = 1, requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @NotNull
    @Schema(description = "비밀번호", example = "1234", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
