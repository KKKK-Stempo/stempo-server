package com.stempo.annotation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

@Component
public class SuccessApiResponseCustomizer implements OperationCustomizer {

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        SuccessApiResponse successApiResponse = handlerMethod.getMethodAnnotation(SuccessApiResponse.class);

        if (successApiResponse != null) {
            // ApiResponse DTO 생성 및 설정
            ApiResponse apiResponse = new ApiResponse()
                    .description(successApiResponse.description())
                    .content(new Content().addMediaType("application/json",
                            new MediaType().schema(createSchema(successApiResponse))));

            // 기존 응답에 추가
            if (operation.getResponses() != null) {
                operation.getResponses().addApiResponse("200", apiResponse);
            }
        }
        return operation;
    }

    private Schema<?> createSchema(SuccessApiResponse successApiResponse) {
        Schema<?> schema = new Schema<>();
        schema.addProperty("success", new BooleanSchema().example(true));

        String data = successApiResponse.data();
        if (!data.isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Object parsedData = objectMapper.readValue(data, Object.class);
                schema.addProperty("data", new Schema<>().example(parsedData));
            } catch (JsonProcessingException e) {
                schema.addProperty("data", new StringSchema().example(data));
            }
        } else {
            schema.addProperty("data", new StringSchema().example(null));
        }

        return schema;
    }
}
