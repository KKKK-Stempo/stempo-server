package com.stempo.api.global.annotation;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
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
        schema.setType("object");
        schema.addProperty("success", new Schema<Boolean>()
                .type("boolean")
                .example(true)
                .description("응답 성공 여부"));
        schema.addProperty("data", new Schema<>()
                .type(successApiResponse.dataType().getSimpleName().toLowerCase())
                .example(successApiResponse.data())
                .description(successApiResponse.dataDescription()));

        return schema;
    }
}
