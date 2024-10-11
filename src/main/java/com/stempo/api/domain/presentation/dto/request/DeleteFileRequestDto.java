package com.stempo.api.domain.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteFileRequestDto {

    @NotBlank(message = "File URL is required")
    @Schema(description = "파일경로", example = "/resources/files/947051880039041_19dea234-b6ec-4c4b-bc92-c53c0d921943.jpg", requiredMode = Schema.RequiredMode.REQUIRED)
    private String url;
}
