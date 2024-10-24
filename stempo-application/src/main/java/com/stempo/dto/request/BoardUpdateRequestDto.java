package com.stempo.dto.request;

import com.stempo.model.BoardCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardUpdateRequestDto {

    @Schema(description = "카테고리", example = "NOTICE", requiredMode = Schema.RequiredMode.REQUIRED)
    private BoardCategory category;

    @Schema(description = "제목", example = "청각 자극을 통한 뇌성마비 환자 보행 패턴 개선 서비스, Stempo.", minLength = 1, maxLength = 100, requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "내용", example = "Stempo는 청각 자극을 통한 뇌성마비 환자 보행 패턴 개선 서비스입니다.", minLength = 1, maxLength = 10000, requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "파일 링크(JSON Array)", example = """
            [
                "/resources/files/947051880039041_19dea234-b6ec-4c4b-bc92-c53c0d921943.wav",
                "/resources/files/boards/1/1030487120626166_1dec3611-c148-4139-bb16-3d2a89ac1dd7.pdf"
            ]
            """)
    private List<String> fileUrls;
}
