package com.stempo.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.stempo.exception.InvalidFieldException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

class PaginationUtilsTest {

    @Test
    void 주어진_정렬_조건으로_아이템을_정렬한다() {
        // given
        List<TestDomain> items = Arrays.asList(
                new TestDomain("B", 2),
                new TestDomain("A", 1),
                new TestDomain("C", 3)
        );
        Sort sort = Sort.by(Sort.Order.asc("validField"));

        // when
        List<TestDomain> sortedItems = PaginationUtils.applySorting(items, sort);

        // then
        assertThat(sortedItems).extracting("validField").containsExactly("A", "B", "C");
    }

    @Test
    void 정렬_조건이_없을때_아이템을_변경하지_않는다() {
        // given
        List<TestDomain> items = Arrays.asList(
                new TestDomain("B", 2),
                new TestDomain("A", 1)
        );
        Sort sort = Sort.unsorted(); // 정렬 조건이 없음

        // when
        List<TestDomain> sortedItems = PaginationUtils.applySorting(items, sort);

        // then
        assertThat(sortedItems).isEqualTo(items);
    }

    @Test
    void 유효하지_않은_정렬_필드를_제공했을때_InvalidColumnException이_발생한다() {
        // given
        List<TestDomain> items = Arrays.asList(
                new TestDomain("B", 2),
                new TestDomain("A", 1)
        );
        Sort sort = Sort.by(Sort.Order.asc("invalidField")); // 유효하지 않은 필드

        // when, then
        assertThatThrownBy(() -> PaginationUtils.applySorting(items, sort))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessageContaining("Invalid field name: invalidField");
    }

    @Test
    void 아이템을_슬라이싱하여_특정_페이지의_아이템을_반환한다() {
        // given
        List<TestDomain> items = Arrays.asList(
                new TestDomain("A", 1),
                new TestDomain("B", 2),
                new TestDomain("C", 3),
                new TestDomain("D", 4)
        );
        Pageable pageable = Pageable.ofSize(2).withPage(1); // 2개씩, 1페이지(2~3번째 아이템)

        // when
        List<TestDomain> slicedItems = PaginationUtils.applySlicing(items, pageable);

        // then
        assertThat(slicedItems).extracting("validField").containsExactly("C", "D");
    }

    @Test
    void 페이지가_초과될때_빈_리스트를_반환한다() {
        // given
        List<TestDomain> items = Arrays.asList(
                new TestDomain("A", 1),
                new TestDomain("B", 2)
        );
        Pageable pageable = Pageable.ofSize(2).withPage(2); // 2개씩, 2페이지(3~4번째 아이템)

        // when
        List<TestDomain> slicedItems = PaginationUtils.applySlicing(items, pageable);

        // then
        assertThat(slicedItems).isEmpty();
    }

    @Test
    void 페이지가_0일때_리스트의_첫부분을_반환한다() {
        // given
        List<TestDomain> items = Arrays.asList(
                new TestDomain("A", 1),
                new TestDomain("B", 2),
                new TestDomain("C", 3)
        );
        Pageable pageable = Pageable.ofSize(2).withPage(0); // 2개씩, 0페이지(0~1번째 아이템)

        // when
        List<TestDomain> slicedItems = PaginationUtils.applySlicing(items, pageable);

        // then
        assertThat(slicedItems).extracting("validField").containsExactly("A", "B");
    }

    @Test
    void 정렬과_슬라이싱을_동시에_적용한다() {
        // given
        List<TestDomain> items = Arrays.asList(
                new TestDomain("B", 2),
                new TestDomain("A", 1),
                new TestDomain("C", 3)
        );
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Order.asc("validField"))); // 2개씩, 0페이지(0~1번째 아이템)

        // when
        List<TestDomain> result = PaginationUtils.applySortingAndSlicing(items, pageable);

        // then
        assertThat(result).extracting("validField").containsExactly("A", "B");
    }

    @Test
    void 필드가_존재하지_않는_경우_isAnySortFieldPresent가_false를_반환한다() {
        // given
        Sort sort = Sort.by(Sort.Order.asc("validField"));
        List<String> fields = List.of("invalidField");

        // when
        boolean result = PaginationUtils.isAnySortFieldPresent(sort, TestDomain.class, fields);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void 필드가_존재하지만_정렬_조건이_없는_경우_isAnySortFieldPresent가_false를_반환한다() {
        // given
        Sort sort = Sort.unsorted(); // 정렬 조건 없음
        List<String> fields = List.of("validField");

        // when
        boolean result = PaginationUtils.isAnySortFieldPresent(sort, TestDomain.class, fields);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void 필드가_존재하고_정렬_조건이_있으면_isAnySortFieldPresent가_true를_반환한다() {
        // given
        Sort sort = Sort.by(Sort.Order.asc("validField")); // 정렬 조건
        List<String> fields = List.of("validField");

        // when
        boolean result = PaginationUtils.isAnySortFieldPresent(sort, TestDomain.class, fields);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 존재하지_않는_클래스에_대한_필드_검사_시_InvalidColumnException이_발생한다() {
        // given
        Sort sort = Sort.by(Sort.Order.asc("invalidField")); // 유효하지 않은 필드
        List<String> fields = List.of("validField");

        // when
        boolean result = PaginationUtils.isAnySortFieldPresent(sort, InvalidClass.class, fields);

        // when, then
        assertThat(result).isFalse();
    }

    static class TestDomain {

        private final String validField;
        private final int anotherField;

        public TestDomain(String validField, int anotherField) {
            this.validField = validField;
            this.anotherField = anotherField;
        }
    }

    static class InvalidClass {

    }
}
