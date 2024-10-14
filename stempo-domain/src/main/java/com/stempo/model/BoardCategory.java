package com.stempo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BoardCategory {

    NOTICE("공지사항"),
    FAQ("자주 묻는 질문"),
    SUGGESTION("건의하기");

    private final String description;
}
