package com.stempo.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDataResponseDto {

    private String deviceTag;
    private List<RecordDataResponseDto> records;
    private List<HomeworkDataResponseDto> homeworks;

    public static UserDataResponseDto of(
        String deviceTag,
        List<RecordDataResponseDto> records,
        List<HomeworkDataResponseDto> homeworks
    ) {
        return UserDataResponseDto.builder()
            .deviceTag(deviceTag)
            .records(records)
            .homeworks(homeworks)
            .build();
    }
}
