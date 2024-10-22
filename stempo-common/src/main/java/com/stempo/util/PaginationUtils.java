package com.stempo.util;

import com.stempo.exception.InvalidFieldException;
import java.util.Comparator;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class PaginationUtils {

    /**
     * 아이템 리스트에 정렬을 적용하는 메서드입니다.
     *
     * @param items 정렬되지 않은 아이템 리스트
     * @param sort  정렬 기준을 포함한 Sort 객체
     * @return 정렬된 아이템 리스트
     */
    public static <T> List<T> applySorting(List<T> items, Sort sort) {
        if (!sort.isSorted()) {
            return items;
        }

        Comparator<T> comparator = sort.stream()
                .map(order -> {
                    Comparator<T> itemComparator = Comparator.comparing(
                            item -> (Comparable) extractFieldValue(item, order.getProperty())
                    );
                    return order.isAscending() ? itemComparator : itemComparator.reversed();
                })
                .reduce(Comparator::thenComparing)
                .orElseThrow(IllegalArgumentException::new);

        return items.stream()
                .sorted(comparator)
                .toList();
    }

    /**
     * 아이템 리스트에 슬라이싱을 적용하는 메서드입니다.
     *
     * @param items    슬라이싱되지 않은 아이템 리스트
     * @param pageable 페이지네이션 정보를 포함하는 Pageable 객체
     * @return 슬라이싱된 아이템 리스트
     */
    public static <T> List<T> applySlicing(List<T> items, Pageable pageable) {
        return items.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .toList();
    }

    /**
     * 아이템 리스트에 정렬과 슬라이싱을 적용하는 메서드입니다.
     *
     * @param items    정렬 및 슬라이싱 되지 않은 아이템 리스트
     * @param pageable 페이지네이션 정보와 정렬 기준을 포함하는 Pageable 객체
     * @return 정렬 및 슬라이싱된 아이템 리스트
     */
    public static <T> List<T> applySortingAndSlicing(List<T> items, Pageable pageable) {
        List<T> sortedItems = applySorting(items, pageable.getSort());
        return applySlicing(sortedItems, pageable);
    }

    /**
     * 주어진 정렬 기준(Sort) 내에서 필드 리스트 중 하나라도 존재하고, 해당 필드들이 모두 특정 클래스의 필드로 존재하는지 확인하는 메소드입니다.
     *
     * @param sort   정렬 기준을 포함한 Sort 객체
     * @param clazz  필드를 검사할 클래스 타입
     * @param fields 검사할 필드 이름들의 리스트
     * @param <T>    클래스 타입
     * @return 클래스에 모든 필드가 존재하고, 정렬 기준에 하나라도 포함된 경우 true를 반환
     */
    public static <T> boolean isAnySortFieldPresent(Sort sort, Class<T> clazz, List<String> fields) {
        boolean allFieldsExistInClass = fields.stream().allMatch(field -> hasFieldInClass(clazz, field));
        boolean anyFieldInSort = fields.stream().anyMatch(field -> sort.getOrderFor(field) != null);

        return allFieldsExistInClass && anyFieldInSort;
    }

    /**
     * 리플렉션을 사용하여 객체의 특정 필드 값을 추출하는 메서드입니다.
     *
     * @param item      값을 추출할 객체
     * @param fieldName 추출할 필드 이름
     * @return 추출된 필드 값
     */
    private static <T> Object extractFieldValue(T item, String fieldName) {
        try {
            var field = item.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(item);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new InvalidFieldException("Invalid field name: " + fieldName);
        }
    }

    /**
     * 특정 클래스 내에 주어진 필드가 존재하는지 확인하는 메소드입니다.
     *
     * @param clazz     필드를 검사할 클래스 타입
     * @param fieldName 확인할 필드 이름
     * @param <T>       클래스 타입
     * @return 필드가 클래스에 존재하는 경우 true를 반환
     */
    private static <T> boolean hasFieldInClass(Class<T> clazz, String fieldName) {
        try {
            clazz.getDeclaredField(fieldName);
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }
}
