package com.stempo.filter;

import com.stempo.util.XssSanitizer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.util.HashMap;
import java.util.Map;

public class XssEscapeServletRequestWrapper extends HttpServletRequestWrapper {

    private final XssSanitizer xssSanitizer;

    public XssEscapeServletRequestWrapper(HttpServletRequest request, XssSanitizer xssSanitizer) {
        super(request);
        this.xssSanitizer = xssSanitizer;
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        return xssSanitizer.sanitize(value);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        return applyFilterToValues(values);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> paramMap = super.getParameterMap();
        Map<String, String[]> sanitizedMap = new HashMap<>(paramMap.size());

        for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
            String[] sanitizedValues = applyFilterToValues(entry.getValue());
            sanitizedMap.put(entry.getKey(), sanitizedValues);
        }

        return sanitizedMap;
    }

    private String[] applyFilterToValues(String[] values) {
        if (values == null) {
            return null;
        }
        String[] sanitizedValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            sanitizedValues[i] = xssSanitizer.sanitize(values[i]);
        }
        return sanitizedValues;
    }
}
