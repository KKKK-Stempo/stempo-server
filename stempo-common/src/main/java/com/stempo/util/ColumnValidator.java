package com.stempo.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import org.springframework.stereotype.Component;

@Component
public class ColumnValidator {

    private ColumnValidator() {
    }

    public static boolean isValidColumn(Class<?> domainClass, String columnName) {
        Field[] fields = domainClass.getDeclaredFields();
        return Arrays.stream(fields)
            .anyMatch(field -> field.getName().equals(columnName));
    }
}
