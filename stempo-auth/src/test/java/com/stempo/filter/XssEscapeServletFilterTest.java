package com.stempo.filter;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.stempo.util.XssSanitizer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class XssEscapeServletFilterTest {

    @Mock
    private XssSanitizer xssSanitizer;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ServletResponse response;

    @Mock
    private FilterChain filterChain;

    private XssEscapeServletFilter xssEscapeServletFilter;

    @BeforeEach
    void setUp() {
        xssEscapeServletFilter = new XssEscapeServletFilter(xssSanitizer);
    }

    @Test
    void Http요청이_필터를_거쳐_Xss필터가_적용된다() throws IOException, ServletException {
        // when
        xssEscapeServletFilter.doFilter(request, response, filterChain);

        // then
        verify(filterChain).doFilter(any(XssEscapeServletRequestWrapper.class), eq(response));
    }

    @Test
    void Http요청이_아닌경우_Xss필터가_적용되지_않고_다음필터로_진행된다() throws IOException, ServletException {
        // given
        ServletRequest nonHttpServletRequest = mock(ServletRequest.class);

        // when
        xssEscapeServletFilter.doFilter(nonHttpServletRequest, response, filterChain);

        // then
        verify(filterChain).doFilter(eq(nonHttpServletRequest), eq(response));
    }
}
