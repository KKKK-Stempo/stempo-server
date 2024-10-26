package com.stempo.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.stempo.annotation.SuccessApiResponseCustomizer;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class OpenApiConfigTest {

    @InjectMocks
    private OpenApiConfig openApiConfig;

    @Mock
    private SuccessApiResponseCustomizer successApiResponseCustomizer;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(openApiConfig, "appVersion", "1.0.0");
    }

    @Test
    void API_정보_빈이_정상적으로_생성되는지_확인한다() {
        // when
        Info apiInfo = openApiConfig.apiInfo();

        // then
        assertThat(apiInfo).isNotNull();
        assertThat(apiInfo.getTitle()).isEqualTo("Stempo");
        assertThat(apiInfo.getVersion()).isEqualTo("1.0.0");
        assertThat(apiInfo.getDescription()).isEqualTo("Stempo API Document");

        Contact contact = apiInfo.getContact();
        assertThat(contact).isNotNull();
        assertThat(contact.getName()).isEqualTo("한관희");
        assertThat(contact.getUrl()).isEqualTo("https://github.com/limehee");
        assertThat(contact.getEmail()).isEqualTo("noop103@naver.com");

        License license = apiInfo.getLicense();
        assertThat(license).isNotNull();
        assertThat(license.getName()).isEqualTo("GNU GENERAL PUBLIC LICENSE v3.0");
        assertThat(license.getUrl()).isEqualTo("https://www.gnu.org/licenses/gpl-3.0.html");
    }

    @Test
    void API_서버_빈이_정상적으로_생성되는지_확인한다() {
        // when
        Server apiServer = openApiConfig.apiServer();

        // then
        assertThat(apiServer).isNotNull();
        assertThat(apiServer.getUrl()).isEqualTo("/");
    }

    @Test
    void 보안_스키마_빈이_정상적으로_생성되는지_확인한다() {
        // when
        SecurityScheme securityScheme = openApiConfig.securityScheme();

        // then
        assertThat(securityScheme).isNotNull();
        assertThat(securityScheme.getName()).isEqualTo("bearerAuth");
        assertThat(securityScheme.getType()).isEqualTo(SecurityScheme.Type.HTTP);
        assertThat(securityScheme.getScheme()).isEqualTo("bearer");
        assertThat(securityScheme.getBearerFormat()).isEqualTo("JWT");
        assertThat(securityScheme.getIn()).isEqualTo(SecurityScheme.In.HEADER);
    }

    @Test
    void 보안_구성_요소_빈이_정상적으로_생성되는지_확인한다() {
        // given
        SecurityScheme securityScheme = openApiConfig.securityScheme();

        // when
        Components components = openApiConfig.apiComponents(securityScheme);

        // then
        assertThat(components).isNotNull();
        assertThat(components.getSecuritySchemes()).containsKey("bearerAuth");
        assertThat(components.getSecuritySchemes().get("bearerAuth")).isEqualTo(securityScheme);
    }

    @Test
    void 보안_요구사항_빈이_정상적으로_생성되는지_확인한다() {
        // when
        SecurityRequirement securityRequirement = openApiConfig.securityRequirement();

        // then
        assertThat(securityRequirement).isNotNull();
        assertThat(securityRequirement.containsKey("bearerAuth")).isTrue();
    }

    @Test
    void OpenAPI_빈이_정상적으로_생성되는지_확인한다() {
        // given
        Info apiInfo = openApiConfig.apiInfo();
        Server apiServer = openApiConfig.apiServer();
        SecurityScheme securityScheme = openApiConfig.securityScheme();
        Components components = openApiConfig.apiComponents(securityScheme);
        SecurityRequirement securityRequirement = openApiConfig.securityRequirement();

        // when
        OpenAPI openAPI = openApiConfig.openAPI(apiInfo, apiServer, components, securityRequirement);

        // then
        assertThat(openAPI).isNotNull();
        assertThat(openAPI.getInfo()).isEqualTo(apiInfo);
        assertThat(openAPI.getServers()).contains(apiServer);
        assertThat(openAPI.getComponents()).isEqualTo(components);
        assertThat(openAPI.getSecurity()).contains(securityRequirement);
    }

    @Test
    void publicApi_빈이_정상적으로_생성되는지_확인한다() {
        // when
        GroupedOpenApi publicApi = openApiConfig.publicApi(successApiResponseCustomizer);

        // then
        assertThat(publicApi).isNotNull();
        assertThat(publicApi.getGroup()).isEqualTo("public");
        assertThat(publicApi.getOperationCustomizers()).contains(successApiResponseCustomizer);
        assertThat(publicApi.getPathsToMatch()).contains("/api/**");
    }
}
