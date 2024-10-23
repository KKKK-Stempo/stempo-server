package com.stempo.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@SpringBootTest(classes = RoleHierarchyConfig.class)
class RoleHierarchyConfigTest {

    @Autowired
    private RoleHierarchy roleHierarchy;

    @Test
    void roleHierarchy_빈이_정상적으로_생성된다() {
        // then
        assertThat(roleHierarchy).isNotNull();
    }

    @Test
    void roleHierarchy_설정이_정상적으로_적용된다() {
        // given
        SimpleGrantedAuthority adminAuthority = new SimpleGrantedAuthority("ROLE_ADMIN");
        SimpleGrantedAuthority userAuthority = new SimpleGrantedAuthority("ROLE_USER");

        // when
        boolean isAdminHigherThanUser = roleHierarchy.getReachableGrantedAuthorities(List.of(adminAuthority))
                .contains(userAuthority);

        // then
        assertThat(isAdminHigherThanUser).isTrue();
    }
}
