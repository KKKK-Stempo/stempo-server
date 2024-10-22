package com.stempo.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class PagedResponseDtoTest {

    @Test
    void Page_객체를_이용한_응답이_정상적으로_생성되는지_확인한다() {
        // given
        List<String> items = List.of("item1", "item2", "item3");
        Page<String> page = new PageImpl<>(items, PageRequest.of(0, 3), 10);

        // when
        PagedResponseDto<String> response = new PagedResponseDto<>(page);

        // then
        assertThat(response.getCurrentPage()).isEqualTo(0);
        assertThat(response.isHasPrevious()).isFalse();
        assertThat(response.isHasNext()).isTrue();
        assertThat(response.getTotalPages()).isEqualTo(4);
        assertThat(response.getTotalItems()).isEqualTo(10);
        assertThat(response.getTake()).isEqualTo(3);
        assertThat(response.getItems()).containsExactly("item1", "item2", "item3");
    }

    @Test
    void Page_객체와_총_아이템_수를_이용한_응답이_정상적으로_생성되는지_확인한다() {
        // given
        List<String> items = List.of("item1", "item2", "item3");
        Page<String> page = new PageImpl<>(items, PageRequest.of(0, 3), 5);

        // when
        PagedResponseDto<String> response = new PagedResponseDto<>(page, 100, 3);

        // then
        assertThat(response.getCurrentPage()).isEqualTo(0);
        assertThat(response.isHasPrevious()).isFalse();
        assertThat(response.isHasNext()).isTrue();
        assertThat(response.getTotalPages()).isEqualTo(2);
        assertThat(response.getTotalItems()).isEqualTo(100);
        assertThat(response.getTake()).isEqualTo(3);
        assertThat(response.getItems()).containsExactly("item1", "item2", "item3");
    }

    @Test
    void List_객체를_이용한_응답이_정상적으로_생성되는지_확인한다() {
        // given
        List<String> items = List.of("item1", "item2", "item3");
        Pageable pageable = PageRequest.of(0, 3);

        // when
        PagedResponseDto<String> response = new PagedResponseDto<>(items, pageable, items.size());

        // then
        assertThat(response.getCurrentPage()).isEqualTo(0);
        assertThat(response.isHasPrevious()).isFalse();
        assertThat(response.isHasNext()).isFalse();
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.getTotalItems()).isEqualTo(3);
        assertThat(response.getTake()).isEqualTo(3);
        assertThat(response.getItems()).containsExactly("item1", "item2", "item3");
    }

    @Test
    void 빈_List_객체를_이용한_응답이_정상적으로_생성되는지_확인한다() {
        // given
        List<String> items = List.of();
        Pageable pageable = PageRequest.of(0, 3);

        // when
        PagedResponseDto<String> response = new PagedResponseDto<>(items, pageable, items.size());

        // then
        assertThat(response.getCurrentPage()).isEqualTo(0);
        assertThat(response.isHasPrevious()).isFalse();
        assertThat(response.isHasNext()).isFalse();
        assertThat(response.getTotalPages()).isEqualTo(0);
        assertThat(response.getTotalItems()).isEqualTo(0);
        assertThat(response.getTake()).isEqualTo(0);
        assertThat(response.getItems()).isEmpty();
    }

    @Test
    void 첫_페이지_응답이_정상적으로_생성되는지_확인한다() {
        // given
        List<String> items = List.of("item1", "item2", "item3");
        Pageable pageable = PageRequest.of(0, 3);

        // when
        PagedResponseDto<String> response = new PagedResponseDto<>(items, pageable, 10);

        // then
        assertThat(response.getCurrentPage()).isEqualTo(0);
        assertThat(response.isHasPrevious()).isFalse(); // 첫 페이지는 이전 페이지가 없어야 함
        assertThat(response.isHasNext()).isTrue(); // 다음 페이지가 존재해야 함
        assertThat(response.getTotalPages()).isEqualTo(4);
        assertThat(response.getTotalItems()).isEqualTo(10);
        assertThat(response.getTake()).isEqualTo(3);
        assertThat(response.getItems()).containsExactly("item1", "item2", "item3");
    }

    @Test
    void 마지막_페이지_응답이_정상적으로_생성되는지_확인한다() {
        // given
        List<String> items = List.of("item7", "item8", "item9", "item10");
        Pageable pageable = PageRequest.of(3, 3); // 4번째 페이지

        // when
        PagedResponseDto<String> response = new PagedResponseDto<>(items, pageable, 10);

        // then
        assertThat(response.getCurrentPage()).isEqualTo(3); // 마지막 페이지
        assertThat(response.isHasPrevious()).isTrue(); // 이전 페이지가 있어야 함
        assertThat(response.isHasNext()).isFalse(); // 마지막 페이지이므로 다음 페이지가 없어야 함
        assertThat(response.getTotalPages()).isEqualTo(4);
        assertThat(response.getTotalItems()).isEqualTo(10);
        assertThat(response.getTake()).isEqualTo(4);
        assertThat(response.getItems()).containsExactly("item7", "item8", "item9", "item10");
    }

    @Test
    void 중간_페이지_응답이_정상적으로_생성되는지_확인한다() {
        // given
        List<String> items = List.of("item4", "item5", "item6");
        Pageable pageable = PageRequest.of(1, 3); // 2번째 페이지

        // when
        PagedResponseDto<String> response = new PagedResponseDto<>(items, pageable, 10);

        // then
        assertThat(response.getCurrentPage()).isEqualTo(1); // 두 번째 페이지
        assertThat(response.isHasPrevious()).isTrue(); // 이전 페이지가 있어야 함
        assertThat(response.isHasNext()).isTrue(); // 다음 페이지도 있어야 함
        assertThat(response.getTotalPages()).isEqualTo(4);
        assertThat(response.getTotalItems()).isEqualTo(10);
        assertThat(response.getTake()).isEqualTo(3);
        assertThat(response.getItems()).containsExactly("item4", "item5", "item6");
    }

    @Test
    void 빈_페이지_응답이_정상적으로_처리되는지_확인한다() {
        // given
        List<String> items = List.of(); // 빈 리스트
        Pageable pageable = PageRequest.of(2, 3);

        // when
        PagedResponseDto<String> response = new PagedResponseDto<>(items, pageable, 10);

        // then
        assertThat(response.getCurrentPage()).isEqualTo(2);
        assertThat(response.isHasPrevious()).isTrue(); // 이전 페이지가 있어야 함
        assertThat(response.isHasNext()).isFalse(); // 빈 페이지이므로 다음 페이지가 없어야 함
        assertThat(response.getTotalPages()).isEqualTo(4);
        assertThat(response.getTotalItems()).isEqualTo(10);
        assertThat(response.getTake()).isEqualTo(0);
        assertThat(response.getItems()).isEmpty();
    }

    @Test
    void 마지막_페이지인지_확인한다() {
        // given
        List<String> items = List.of("item1", "item2", "item3");
        Pageable pageable = PageRequest.of(1, 3); // 2번째 페이지, 페이지당 3개
        int totalItems = 6; // 전체 6개 아이템이 있다고 가정

        // when
        PagedResponseDto<String> response = new PagedResponseDto<>(items, pageable, totalItems);

        // then
        assertThat(response.isHasNext()).isFalse(); // 마지막 페이지이므로 hasNext는 false여야 한다
        assertThat(response.isHasPrevious()).isTrue(); // 이전 페이지는 존재해야 한다
    }
}
