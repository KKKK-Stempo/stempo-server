package com.stempo.util;

import com.stempo.exception.BaseException;
import com.stempo.exception.ErrorCode;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PageableUtils {

    public static Pageable createPageable(int page, int size, List<String> sortByList, List<String> sortDirectionList,
            Class<?> domainClass) {
        if (sortByList.size() != sortDirectionList.size()) {
            throw new BaseException(ErrorCode.SORTING_ARGUMENT_ERROR);
        }

        for (String sortBy : sortByList) {
            if (!ColumnValidator.isValidColumn(domainClass, sortBy)) {
                log.error("Invalid field: {}", sortBy);
                throw new BaseException(ErrorCode.INVALID_FIELD);
            }
        }

        for (String direction : sortDirectionList) {
            if (!isValidateSortDirection(direction)) {
                log.error("Invalid sorting direction: {}", direction);
                throw new BaseException(ErrorCode.SORTING_ARGUMENT_ERROR);
            }
        }

        Sort sort = Sort.by(
                IntStream.range(0, sortByList.size())
                        .mapToObj(i -> new Sort.Order(Sort.Direction.fromString(sortDirectionList.get(i)),
                                sortByList.get(i)))
                        .collect(Collectors.toList())
        );

        return PageRequest.of(page, size, sort);
    }

    private static boolean isValidateSortDirection(String direction) {
        return "asc".equalsIgnoreCase(direction) || "desc".equalsIgnoreCase(direction);
    }
}
