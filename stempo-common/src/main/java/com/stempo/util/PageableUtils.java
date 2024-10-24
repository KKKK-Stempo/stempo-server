package com.stempo.util;

import com.stempo.exception.InvalidFieldException;
import com.stempo.exception.SortingArgumentException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class PageableUtils {

    public static Pageable createPageable(int page, int size, List<String> sortByList, List<String> sortDirectionList,
            Class<?> domainClass) {
        if (sortByList.size() != sortDirectionList.size()) {
            throw new SortingArgumentException();
        }

        for (String sortBy : sortByList) {
            if (!ColumnValidator.isValidColumn(domainClass, sortBy)) {
                throw new InvalidFieldException(sortBy + " is not a valid column.");
            }
        }

        for (String direction : sortDirectionList) {
            if (!isValidateSortDirection(direction)) {
                throw new SortingArgumentException(direction + " is not a valid sorting direction.");
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
