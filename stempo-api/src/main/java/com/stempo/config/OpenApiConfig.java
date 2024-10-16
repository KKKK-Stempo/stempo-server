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
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI(@Value("${springdoc.version}") String appVersion) {
        Info info = new Info().title("Stempo").version(appVersion)
                .description("Stempo API Document")
                .contact(new Contact().name("한관희").url("https://github.com/limehee").email("noop103@naver.com"))
                .license(new License().name("GNU GENERAL PUBLIC LICENSE v3.0").url("https://www.gnu.org/licenses/gpl-3.0.html"));

        final String securitySchemeName = "bearerAuth";
        Server server = new Server().url("/");

        return new OpenAPI()
                .servers(List.of(server))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .in(SecurityScheme.In.HEADER)
                                )
                )
                .info(info);
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
