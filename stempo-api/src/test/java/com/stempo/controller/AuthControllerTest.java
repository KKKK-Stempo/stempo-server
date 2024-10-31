package com.stempo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stempo.dto.TokenInfo;
import com.stempo.dto.request.AuthRequestDto;
import com.stempo.dto.request.TwoFactorAuthenticationRequestDto;
import com.stempo.exception.BaseException;
import com.stempo.exception.ErrorCode;
import com.stempo.service.AuthService;
import com.stempo.test.TestApplication;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AuthController.class)
@ContextConfiguration(classes = TestApplication.class)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 정상적으로_회원가입을_한다() throws Exception {
        // given
        AuthRequestDto requestDto = new AuthRequestDto();
        requestDto.setDeviceTag("490154203237518");
        requestDto.setPassword("password123");

        TokenInfo tokenInfo = TokenInfo.create("access-token", "refresh-token");

        when(authService.registerUser(any(AuthRequestDto.class)))
                .thenReturn(tokenInfo);

        // when
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"));
    }

    @Test
    void 유효하지_않은_데이터로_회원가입을_시도하면_예외가_발생한다() throws Exception {
        // given
        AuthRequestDto requestDto = new AuthRequestDto();
        requestDto.setPassword("password123");

        // when
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void 이미_존재하는_사용자로_회원가입을_시도하면_예외가_발생한다() throws Exception {
        // given
        AuthRequestDto requestDto = new AuthRequestDto();
        requestDto.setDeviceTag("490154203237518");
        requestDto.setPassword("password123");

        when(authService.registerUser(any(AuthRequestDto.class)))
                .thenThrow(new BaseException(ErrorCode.USER_ALREADY_EXISTS, "사용자가 이미 존재합니다."));

        // when
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                // then
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @WithMockUser(roles = "USER")
    void 정상적으로_회원탈퇴를_한다() throws Exception {
        // given
        String deviceTag = "490154203237518";

        when(authService.unregisterUser())
                .thenReturn(deviceTag);

        // when
        mockMvc.perform(delete("/api/v1/auth/unregister")
                        .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(deviceTag));
    }

    @Test
    void 인증되지_않은_사용자가_회원탈퇴를_시도하면_권한에러가_발생한다() throws Exception {
        // when
        mockMvc.perform(delete("/api/v1/auth/unregister")
                        .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 정상적으로_로그인한다() throws Exception {
        // given
        AuthRequestDto requestDto = new AuthRequestDto();
        requestDto.setDeviceTag("490154203237518");
        requestDto.setPassword("password123");

        TokenInfo tokenInfo = TokenInfo.create("access-token", "refresh-token");

        when(authService.login(any(AuthRequestDto.class)))
                .thenReturn(tokenInfo);

        // when
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"));
    }

    @Test
    void 잘못된_자격증명으로_로그인_시도하면_예외가_발생한다() throws Exception {
        // given
        AuthRequestDto requestDto = new AuthRequestDto();
        requestDto.setDeviceTag("490154203237518");
        requestDto.setPassword("wrongpassword");

        when(authService.login(any(AuthRequestDto.class)))
                .thenThrow(new BaseException(ErrorCode.BAD_CREDENTIALS, "잘못된 자격 증명입니다."));

        // when
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                // then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void 인증되지_않은_사용자가_로그인을_시도한다() throws Exception {
        // given
        AuthRequestDto requestDto = new AuthRequestDto();
        requestDto.setDeviceTag("490154203237518");
        requestDto.setPassword("password123");

        TokenInfo tokenInfo = TokenInfo.create("access-token", "refresh-token");

        when(authService.login(any(AuthRequestDto.class)))
                .thenReturn(tokenInfo);

        // when
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void 정상적으로_토큰을_재발급한다() throws Exception {
        // given
        TokenInfo tokenInfo = TokenInfo.create("new-access-token", "new-refresh-token");

        when(authService.reissueToken(any(HttpServletRequest.class)))
                .thenReturn(tokenInfo);

        // when
        mockMvc.perform(post("/api/v1/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("new-refresh-token"));
    }

    @Test
    void 인증되지_않은_사용자가_토큰_재발급을_시도하면_권한에error가_발생한다() throws Exception {
        // when
        mockMvc.perform(post("/api/v1/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void 정상적으로_TOTP_인증을_한다() throws Exception {
        // given
        TwoFactorAuthenticationRequestDto requestDto = new TwoFactorAuthenticationRequestDto();
        requestDto.setDeviceTag("490154203237518");
        requestDto.setTotp("123456");

        TokenInfo tokenInfo = TokenInfo.create("access-token", "refresh-token");

        when(authService.authenticate(any(TwoFactorAuthenticationRequestDto.class)))
                .thenReturn(tokenInfo);

        // when
        mockMvc.perform(post("/api/v1/auth/two-factor-authentication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void 잘못된_TOTP_코드로_인증을_시도하면_예외가_발생한다() throws Exception {
        // given
        TwoFactorAuthenticationRequestDto requestDto = new TwoFactorAuthenticationRequestDto();
        requestDto.setDeviceTag("490154203237518");
        requestDto.setTotp("000000");

        when(authService.authenticate(any(TwoFactorAuthenticationRequestDto.class)))
                .thenThrow(new BaseException(ErrorCode.BAD_CREDENTIALS, "잘못된 TOTP 코드입니다."));

        // when
        mockMvc.perform(post("/api/v1/auth/two-factor-authentication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                // then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void 인증되지_않은_사용자가_TOTP_인증을_시도한다() throws Exception {
        // given
        TwoFactorAuthenticationRequestDto requestDto = new TwoFactorAuthenticationRequestDto();
        requestDto.setDeviceTag("490154203237518");
        requestDto.setTotp("123456");

        TokenInfo tokenInfo = TokenInfo.create("access-token", "refresh-token");

        when(authService.authenticate(any(TwoFactorAuthenticationRequestDto.class)))
                .thenReturn(tokenInfo);

        // when
        mockMvc.perform(post("/api/v1/auth/two-factor-authentication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 정상적으로_TOTP_초기화한다() throws Exception {
        // given
        String deviceTag = "490154203237518";
        String returnedDeviceTag = "490154203237518";

        when(authService.resetAuthenticator(eq(deviceTag)))
                .thenReturn(returnedDeviceTag);

        // when
        mockMvc.perform(delete("/api/v1/auth/two-factor-authentication/{deviceTag}", deviceTag)
                        .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(returnedDeviceTag));
    }

    @Test
    @WithMockUser(roles = "USER")
    void 비관리자가_TOTP_초기화를_시도하면_권한에러가_발생한다() throws Exception {
        // given
        String deviceTag = "490154203237518";

        // when
        mockMvc.perform(delete("/api/v1/auth/two-factor-authentication/{deviceTag}", deviceTag)
                        .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isForbidden());
    }

    @Test
    void 인증되지_않은_사용자가_TOTP_초기화를_시도하면_권한에러가_발생한다() throws Exception {
        // given
        String deviceTag = "490154203237518";

        // when
        mockMvc.perform(delete("/api/v1/auth/two-factor-authentication/{deviceTag}", deviceTag)
                        .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void 토큰_재발급시_잘못된_리프레시_토큰으로_인해_예외가_발생한다() throws Exception {
        // given
        when(authService.reissueToken(any(HttpServletRequest.class)))
                .thenThrow(new BaseException(ErrorCode.TOKEN_INVALID, "잘못된 리프레시 토큰입니다."));

        // when
        mockMvc.perform(post("/api/v1/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").isEmpty());
    }

}
