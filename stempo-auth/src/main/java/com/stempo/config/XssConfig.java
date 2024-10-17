package com.stempo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stempo.filter.XssEscapeServletFilter;
import com.stempo.util.HtmlCharacterEscapes;
import com.stempo.util.XssSanitizer;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
@RequiredArgsConstructor
public class XssConfig {

    private final ObjectMapper mapper;
    private final XssSanitizer xssSanitizer;

    // JSON 응답에 대해 HTML 이스케이프 필터 적용
    @Bean
    public MappingJackson2HttpMessageConverter characterEscapeConverter() {
        ObjectMapper objectMapper = mapper.copy();
        objectMapper.getFactory().setCharacterEscapes(new HtmlCharacterEscapes());
        return new MappingJackson2HttpMessageConverter(objectMapper);
    }

    // 요청 파라미터 및 폼 데이터에 대한 필터 적용
    @Bean
    public FilterRegistrationBean<Filter> xssFilter() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new XssEscapeServletFilter(xssSanitizer));
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
