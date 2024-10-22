package com.stempo.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.stempo.exception.InvalidFieldException;
import com.stempo.exception.SortingArgumentException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

class PageableUtilsTest {

    @Test
    void 주어진_정렬_조건으로_페이지네이션_객체를_생성한다() {
        // given
        int page = 0;
        int size = 10;
        List<String> sortByList = Arrays.asList("validField", "anotherField");
        List<String> sortDirectionList = Arrays.asList("asc", "desc");

        // when
        Pageable pageable = PageableUtils.createPageable(page, size, sortByList, sortDirectionList, TestDomain.class);

        // then
        assertThat(pageable.getPageNumber()).isEqualTo(page);
        assertThat(pageable.getPageSize()).isEqualTo(size);
        assertThat(pageable.getSort()).isEqualTo(Sort.by(
                Sort.Order.asc("validField"),
                Sort.Order.desc("anotherField")
        ));
    }

    @Test
    void 유효하지_않은_정렬_필드를_제공했을때_InvalidColumnException이_발생한다() {
        // given
        int page = 0;
        int size = 10;
        List<String> sortByList = List.of("invalidField"); // 유효하지 않은 필드
        List<String> sortDirectionList = List.of("asc");

        // when, then
        assertThatThrownBy(
                () -> PageableUtils.createPageable(page, size, sortByList, sortDirectionList, TestDomain.class))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessageContaining("invalidField is not a valid column.");
    }

    @Test
    void 유효하지_않은_정렬_방향을_제공했을때_SortingArgumentException이_발생한다() {
        // given
        int page = 0;
        int size = 10;
        List<String> sortByList = List.of("validField");
        List<String> sortDirectionList = List.of("invalidDirection"); // 유효하지 않은 방향

        // when, then
        assertThatThrownBy(
                () -> PageableUtils.createPageable(page, size, sortByList, sortDirectionList, TestDomain.class))
                .isInstanceOf(SortingArgumentException.class)
                .hasMessageContaining("invalidDirection is not a valid sorting direction.");
    }

    @Test
    void 정렬_필드와_정렬_방향의_크기가_다를때_SortingArgumentException이_발생한다() {
        // given
        int page = 0;
        int size = 10;
        List<String> sortByList = List.of("validField");
        List<String> sortDirectionList = Arrays.asList("asc", "desc"); // 크기 불일치

        // when, then
        assertThatThrownBy(
                () -> PageableUtils.createPageable(page, size, sortByList, sortDirectionList, TestDomain.class))
                .isInstanceOf(SortingArgumentException.class);
    }

    static class TestDomain {

        private String validField;
        private int anotherField;
    }
}
