package com.stempo.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stempo.model.CustomUserDetails;
import com.stempo.model.Role;
import com.stempo.model.User;
import com.stempo.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 유저가_정상적으로_로드되는지_확인한다() {
        // given
        String deviceTag = "deviceTag123";
        User user = User.create(deviceTag, "password123");
        when(userRepository.findById(deviceTag)).thenReturn(Optional.of(user));

        // when
        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(deviceTag);

        // then
        assertNotNull(userDetails);
        assertEquals(deviceTag, userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(Role.USER.name())));
        verify(userRepository, times(1)).findById(deviceTag);
    }

    @Test
    void 존재하지_않는_유저일_경우_예외를_던진다() {
        // given
        String deviceTag = "nonExistentDeviceTag";
        when(userRepository.findById(deviceTag)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(deviceTag))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("[User] id: " + deviceTag + " not found");

        verify(userRepository, times(1)).findById(deviceTag);
    }
}
