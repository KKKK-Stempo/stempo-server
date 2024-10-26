package com.stempo.config;

import com.stempo.annotation.SuccessApiResponseCustomizer;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {

    @Value("${springdoc.version}")
    private String appVersion;

    @Bean
    public Info apiInfo() {
        return new Info()
                .title("Stempo")
                .version(appVersion)
                .description("Stempo API Document")
                .contact(new Contact()
                        .name("한관희")
                        .url("https://github.com/limehee")
                        .email("noop103@naver.com"))
                .license(new License()
                        .name("GNU GENERAL PUBLIC LICENSE v3.0")
                        .url("https://www.gnu.org/licenses/gpl-3.0.html"));
    }

    @Bean
    public Server apiServer() {
        return new Server().url("/");
    }

    @Bean
    public SecurityScheme securityScheme() {
        return new SecurityScheme()
                .name("bearerAuth")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER);
    }

    @Bean
    public Components apiComponents(SecurityScheme securityScheme) {
        return new Components().addSecuritySchemes("bearerAuth", securityScheme);
    }

    @Bean
    public SecurityRequirement securityRequirement() {
        return new SecurityRequirement().addList("bearerAuth");
    }

    @Bean
    public OpenAPI openAPI(Info apiInfo, Server apiServer, Components apiComponents,
            SecurityRequirement securityRequirement) {
        return new OpenAPI()
                .info(apiInfo)
                .servers(List.of(apiServer))
                .components(apiComponents)
                .addSecurityItem(securityRequirement);
    }

    @Bean
    public GroupedOpenApi publicApi(SuccessApiResponseCustomizer successApiResponseCustomizer) {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/**")
                .addOperationCustomizer(successApiResponseCustomizer)
                .build();
    }
}
