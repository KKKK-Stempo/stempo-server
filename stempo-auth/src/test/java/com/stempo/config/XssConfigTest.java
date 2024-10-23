package com.stempo.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stempo.filter.XssEscapeServletFilter;
import com.stempo.util.HtmlCharacterEscapes;
import com.stempo.util.XssSanitizer;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@SpringBootTest(classes = XssConfig.class)
@Import({ObjectMapper.class, XssSanitizer.class})
class XssConfigTest {

    @Autowired
    private MappingJackson2HttpMessageConverter characterEscapeConverter;

    @Autowired
    private FilterRegistrationBean<Filter> xssFilter;

    @Autowired
    private XssSanitizer xssSanitizer;

    @Test
    void characterEscapeConverter_빈이_정상적으로_생성된다() {
        // then
        assertThat(characterEscapeConverter).isNotNull();
    }

    @Test
    void xssFilter_빈이_정상적으로_생성된다() {
        // then
        assertThat(xssFilter).isNotNull();
        assertThat(xssFilter.getFilter()).isInstanceOf(XssEscapeServletFilter.class);
    }

    @Test
    void characterEscapeConverter_설정이_정상적으로_적용된다() {
        // when
        ObjectMapper objectMapper = characterEscapeConverter.getObjectMapper();

        // then
        assertThat(objectMapper.getFactory().getCharacterEscapes()).isInstanceOf(HtmlCharacterEscapes.class);
    }

    @Test
    void xssFilter_설정이_정상적으로_적용된다() {
        // when
        XssEscapeServletFilter filter = (XssEscapeServletFilter) xssFilter.getFilter();

        // then
        assertThat(filter).hasFieldOrPropertyWithValue("xssSanitizer", xssSanitizer);
        assertThat(xssFilter.getOrder()).isEqualTo(1);
    }
}
