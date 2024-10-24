package com.stempo.filter;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stempo.util.XssSanitizer;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class XssEscapeServletRequestWrapperTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private XssSanitizer xssSanitizer;

    private XssEscapeServletRequestWrapper xssEscapeServletRequestWrapper;

    @BeforeEach
    void setUp() {
        xssEscapeServletRequestWrapper = new XssEscapeServletRequestWrapper(request, xssSanitizer);
    }

    @Test
    void 파라미터가_Xss필터링되어_반환된다() {
        // given
        String rawValue = "<script>alert('XSS')</script>";
        String sanitizedValue = "alert('XSS')";

        when(request.getParameter("param")).thenReturn(rawValue);
        when(xssSanitizer.sanitize(rawValue)).thenReturn(sanitizedValue);

        // when
        String result = xssEscapeServletRequestWrapper.getParameter("param");

        // then
        assertEquals(sanitizedValue, result);
        verify(xssSanitizer).sanitize(rawValue);
    }

    @Test
    void 여러_파라미터가_Xss필터링되어_반환된다() {
        // given
        String[] rawValues = {"<script>", "<b>bold</b>"};
        String[] sanitizedValues = {"", "bold"};

        when(request.getParameterValues("param")).thenReturn(rawValues);
        when(xssSanitizer.sanitize("<script>")).thenReturn("");
        when(xssSanitizer.sanitize("<b>bold</b>")).thenReturn("bold");

        // when
        String[] result = xssEscapeServletRequestWrapper.getParameterValues("param");

        // then
        assertArrayEquals(sanitizedValues, result);
        verify(xssSanitizer).sanitize("<script>");
        verify(xssSanitizer).sanitize("<b>bold</b>");
    }

    @Test
    void 모든_파라미터가_Xss필터링되어_맵으로_반환된다() {
        // given
        Map<String, String[]> rawMap = new HashMap<>();
        rawMap.put("param1", new String[]{"<script>"});
        rawMap.put("param2", new String[]{"<b>bold</b>"});

        when(request.getParameterMap()).thenReturn(rawMap);
        when(xssSanitizer.sanitize("<script>")).thenReturn("");
        when(xssSanitizer.sanitize("<b>bold</b>")).thenReturn("bold");

        // when
        Map<String, String[]> result = xssEscapeServletRequestWrapper.getParameterMap();

        // then
        assertEquals(2, result.size());
        assertArrayEquals(new String[]{""}, result.get("param1"));
        assertArrayEquals(new String[]{"bold"}, result.get("param2"));
        verify(xssSanitizer).sanitize("<script>");
        verify(xssSanitizer).sanitize("<b>bold</b>");
    }
}
