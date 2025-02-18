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
}
