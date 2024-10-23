package com.stempo.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stempo.dto.TokenInfo;
import com.stempo.model.Role;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

class JwtTokenServiceImplTest {

    @InjectMocks
    private JwtTokenServiceImpl jwtTokenServiceImpl;

    @Mock
    private JwtTokenGenerator tokenGenerator;

    @Mock
    private JwtTokenParser tokenParser;

    @Mock
    private JwtTokenValidator tokenValidator;

    @Mock
    private Authentication authentication;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 인증정보로_토큰을_생성한다() {
        // given
        TokenInfo tokenInfo = TokenInfo.create("accessToken", "refreshToken");
        when(tokenGenerator.generateToken(authentication)).thenReturn(tokenInfo);

        // when
        TokenInfo result = jwtTokenServiceImpl.generateToken(authentication);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("accessToken");
        assertThat(result.getRefreshToken()).isEqualTo("refreshToken");
        verify(tokenGenerator, times(1)).generateToken(authentication);
    }

    @Test
    void 아이디와_역할로_토큰을_생성한다() {
        // given
        String userId = "testUser";
        Role role = Role.ADMIN;
        TokenInfo tokenInfo = TokenInfo.create("accessToken", "refreshToken");
        when(tokenGenerator.generateToken(userId, role)).thenReturn(tokenInfo);

        // when
        TokenInfo result = jwtTokenServiceImpl.generateToken(userId, role);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("accessToken");
        assertThat(result.getRefreshToken()).isEqualTo("refreshToken");
        verify(tokenGenerator, times(1)).generateToken(userId, role);
    }

    @Test
    void 토큰으로_인증정보를_가져온다() {
        // given
        String token = "testToken";
        when(tokenParser.getAuthentication(token)).thenReturn(authentication);

        // when
        Authentication result = jwtTokenServiceImpl.getAuthentication(token);

        // then
        assertThat(result).isNotNull();
        verify(tokenParser, times(1)).getAuthentication(token);
    }

    @Test
    void 요청에서_토큰을_추출한다() {
        // given
        String bearerToken = "Bearer testToken";
        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(tokenParser.resolveToken(request)).thenReturn("testToken");

        // when
        String result = jwtTokenServiceImpl.resolveToken(request);

        // then
        assertThat(result).isEqualTo("testToken");
        verify(tokenParser, times(1)).resolveToken(request);
    }

    @Test
    void 토큰을_검증한다() {
        // given
        String token = "testToken";
        when(tokenValidator.validateToken(token)).thenReturn(true);

        // when
        boolean result = jwtTokenServiceImpl.validateToken(token);

        // then
        assertThat(result).isTrue();
        verify(tokenValidator, times(1)).validateToken(token);
    }

    @Test
    void 토큰을_조용히_검증한다() {
        // given
        String token = "testToken";
        when(tokenValidator.validateTokenSilently(token)).thenReturn(true);

        // when
        boolean result = jwtTokenServiceImpl.validateTokenSilently(token);

        // then
        assertThat(result).isTrue();
        verify(tokenValidator, times(1)).validateTokenSilently(token);
    }

    @Test
    void 리프레시_토큰인지_확인한다() {
        // given
        String token = "testToken";
        when(tokenValidator.isRefreshToken(token)).thenReturn(true);

        // when
        boolean result = jwtTokenServiceImpl.isRefreshToken(token);

        // then
        assertThat(result).isTrue();
        verify(tokenValidator, times(1)).isRefreshToken(token);
    }
}
