package com.stempo.api.global.util;

import com.stempo.api.global.exception.InvalidColumnException;
import com.stempo.api.global.exception.SortingArgumentException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class PageableUtil {

    private final ColumnValidator columnValidator;

    public PageableUtil(ColumnValidator columnValidator) {
        this.columnValidator = columnValidator;
    }

    public Pageable createPageable(int page, int size, List<String> sortByList, List<String> sortDirectionList, Class<?> domainClass) throws SortingArgumentException, InvalidColumnException {
        if (sortByList.size() != sortDirectionList.size()) {
            throw new SortingArgumentException();
        }

        for (String sortBy : sortByList) {
            if (!columnValidator.isValidColumn(domainClass, sortBy)) {
                throw new InvalidColumnException(sortBy + " is not a valid column.");
            }
        }

        for (String direction : sortDirectionList) {
            if (!isValidateSortDirection(direction)) {
                throw new SortingArgumentException(direction + " is not a valid sorting direction.");
            }
        }

        Sort sort = Sort.by(
                IntStream.range(0, sortByList.size())
                        .mapToObj(i -> new Sort.Order(Sort.Direction.fromString(sortDirectionList.get(i)), sortByList.get(i)))
                        .collect(Collectors.toList())
        );

        return PageRequest.of(page, size, sort);
    }

    private boolean isValidateSortDirection(String direction) {
        return "asc".equalsIgnoreCase(direction) || "desc".equalsIgnoreCase(direction);
    }
}
