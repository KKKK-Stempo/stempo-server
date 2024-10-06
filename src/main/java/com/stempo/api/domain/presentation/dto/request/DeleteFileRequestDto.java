package com.stempo.api.domain.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteFileRequestDto {

    @NotBlank(message = "File URL is required")
    @Schema(description = "파일경로", example = "/resources/files/boards/123456.png", requiredMode = Schema.RequiredMode.REQUIRED)
    private String url;
}
