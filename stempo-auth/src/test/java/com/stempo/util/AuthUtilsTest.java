package com.stempo.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.stempo.exception.AuthenticationNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class AuthUtilsTest {

    @Test
    @WithMockUser(username = "testDeviceTag")
    void 인증정보가_정상적으로_존재하는지_확인한다() {
        // when
        User result = AuthUtils.getAuthenticationInfo();

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testDeviceTag");
    }

    @Test
    void 인증정보가_존재하지_않으면_예외를_던진다() {
        // SecurityContext에 인증 정보가 없는 경우
        SecurityContextHolder.clearContext(); // 인증 정보 초기화

        // when, then
        assertThatThrownBy(AuthUtils::getAuthenticationInfo)
                .isInstanceOf(AuthenticationNotFoundException.class)
                .hasMessage("SecurityContext에서 인증 정보를 찾을 수 없습니다.");
    }

    @Test
    void 인증정보에_이름이_없으면_예외를_던진다() {
        // given
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        // SecurityContext에 Authentication 설정, 하지만 이름은 null로 설정
        when(authentication.getName()).thenReturn(null);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // when, then
        assertThatThrownBy(AuthUtils::getAuthenticationInfo)
                .isInstanceOf(AuthenticationNotFoundException.class)
                .hasMessage("인증된 사용자의 이름이 없습니다.");
    }

    @Test
    @WithMockUser(username = "testDeviceTag")
    void 인증정보에서_디바이스_태그를_정상적으로_가져온다() {
        // when
        String deviceTag = AuthUtils.getAuthenticationInfoDeviceTag();

        // then
        assertThat(deviceTag).isEqualTo("testDeviceTag");
    }
}
