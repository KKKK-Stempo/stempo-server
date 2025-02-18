package com.stempo.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.method.HandlerMethod;

@ExtendWith(MockitoExtension.class)
class SuccessApiResponseCustomizerTest {

    @InjectMocks
    private SuccessApiResponseCustomizer customizer;

    @Mock
    private HandlerMethod handlerMethod;

    private Operation operation;

    @BeforeEach
    void setUp() {
        operation = new Operation();
        operation.setResponses(new ApiResponses());
    }

    @Test
    void SuccessApiResponse_애노테이션이_적용된_경우_Operation을_커스터마이징한다() throws NoSuchMethodException {
        // given
        Method testMethod = this.getClass().getDeclaredMethod("annotatedTestMethod");
        SuccessApiResponse successApiResponse = testMethod.getAnnotation(SuccessApiResponse.class);
        when(handlerMethod.getMethodAnnotation(SuccessApiResponse.class)).thenReturn(successApiResponse);

        // when
        customizer.customize(operation, handlerMethod);

        // then
        ApiResponse apiResponse = operation.getResponses().get("200");
        assertThat(apiResponse).isNotNull();
        assertThat(apiResponse.getDescription()).isEqualTo("Success");

        // 애노테이션 값 검증
        assertThat(successApiResponse.description()).isEqualTo("Success");
        assertThat(successApiResponse.data()).isEqualTo("test-data");
        assertThat(successApiResponse.dataType()).isEqualTo(String.class);
        assertThat(successApiResponse.dataDescription()).isEqualTo("test-data-description");

        // 'success' 필드 검증
        Schema<?> successSchema = (Schema<?>) apiResponse.getContent()
            .get("application/json")
            .getSchema()
            .getProperties()
            .get("success");

        assertThat(successSchema).isNotNull();
        assertThat(successSchema.getExample()).isEqualTo(true);

        // 'data' 필드 검증
        Schema<?> dataSchema = (Schema<?>) apiResponse.getContent()
            .get("application/json")
            .getSchema()
            .getProperties()
            .get("data");

        assertThat(dataSchema).isNotNull();
        assertThat(dataSchema.getExample()).isEqualTo("test-data");
        assertThat(dataSchema.getType()).isEqualTo("string");
    }

    @Test
    void SuccessApiResponse_애노테이션이_없는_경우_Operation을_커스터마이징하지_않는다() {
        // when
        customizer.customize(operation, handlerMethod);

        // then
        assertThat(operation.getResponses().get("200")).isNull();
    }

    @Test
    void SuccessApiResponse_애노테이션의_data가_유효한_JSON인_경우_파싱하여_반환한다() throws NoSuchMethodException {
        // given
        Method testMethod = this.getClass().getDeclaredMethod("annotatedJsonTestMethod");
        SuccessApiResponse successApiResponse = testMethod.getAnnotation(SuccessApiResponse.class);
        when(handlerMethod.getMethodAnnotation(SuccessApiResponse.class)).thenReturn(successApiResponse);

        // when
        customizer.customize(operation, handlerMethod);

        // then
        ApiResponse apiResponse = operation.getResponses().get("200");
        assertThat(apiResponse).isNotNull();

        Schema<?> dataSchema = (Schema<?>) apiResponse.getContent()
            .get("application/json")
            .getSchema()
            .getProperties()
            .get("data");
        assertThat(dataSchema).isNotNull();

        Object example = dataSchema.getExample();
        assertThat(example).isInstanceOf(Map.class);
        @SuppressWarnings("unchecked")
        Map<String, Object> exampleMap = (Map<String, Object>) example;
        Map<String, Object> expectedMap = new LinkedHashMap<>();
        expectedMap.put("key", "value");
        assertThat(exampleMap).isEqualTo(expectedMap);
    }

    @Test
    void SuccessApiResponse_애노테이션의_data가_빈문자열인_경우_null로_설정한다() throws NoSuchMethodException {
        // given
        Method testMethod = this.getClass().getDeclaredMethod("annotatedEmptyDataTestMethod");
        SuccessApiResponse successApiResponse = testMethod.getAnnotation(SuccessApiResponse.class);
        when(handlerMethod.getMethodAnnotation(SuccessApiResponse.class)).thenReturn(successApiResponse);

        // when
        customizer.customize(operation, handlerMethod);

        // then
        ApiResponse apiResponse = operation.getResponses().get("200");
        assertThat(apiResponse).isNotNull();
        Schema<?> dataSchema = (Schema<?>) apiResponse.getContent()
            .get("application/json")
            .getSchema()
            .getProperties()
            .get("data");
        assertThat(dataSchema).isNotNull();
        assertThat(dataSchema.getExample()).isNull();
    }

    // 기존 테스트용 애노테이션 (유효하지 않은 JSON인 경우)
    @SuccessApiResponse(
        description = "Success",
        data = "test-data",
        dataType = String.class,
        dataDescription = "test-data-description")
    private void annotatedTestMethod() {
    }

    // data가 유효한 JSON인 경우
    @SuccessApiResponse(
        description = "Success JSON",
        data = "{\"key\": \"value\"}",
        dataType = Map.class,
        dataDescription = "JSON data description")
    private void annotatedJsonTestMethod() {
    }

    // data가 빈 문자열인 경우
    @SuccessApiResponse(
        description = "Success Empty",
        data = "",
        dataType = Void.class,
        dataDescription = "Empty data")
    private void annotatedEmptyDataTestMethod() {
    }
}
