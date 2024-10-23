package com.stempo.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

class IpWhitelistValidatorTest {

    private WhitelistFileLoader whitelistFileLoader;
    private IpWhitelistValidator ipWhitelistValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        whitelistFileLoader = mock(WhitelistFileLoader.class);
    }

    @Test
    void 화이트리스트가_비활성화된_경우_항상_허용한다() {
        // given
        ipWhitelistValidator = new IpWhitelistValidator(false, whitelistFileLoader);

        // when
        boolean result = ipWhitelistValidator.isIpWhitelisted("192.168.1.1");

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 요청된_IP가_화이트리스트에_포함된_경우_허용한다() {
        // given
        ipWhitelistValidator = new IpWhitelistValidator(true, whitelistFileLoader);
        List<String> whitelistIps = Arrays.asList("192.168.1.1", "10.0.0.0/24");
        when(whitelistFileLoader.loadWhitelistIps()).thenReturn(whitelistIps);

        // when
        boolean result = ipWhitelistValidator.isIpWhitelisted("192.168.1.1");

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 요청된_IP가_서브넷에_포함된_경우_허용한다() {
        // given
        ipWhitelistValidator = new IpWhitelistValidator(true, whitelistFileLoader);
        List<String> whitelistIps = Collections.singletonList("192.168.1.0/24");
        when(whitelistFileLoader.loadWhitelistIps()).thenReturn(whitelistIps);

        // when
        boolean result = ipWhitelistValidator.isIpWhitelisted("192.168.1.50");

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 요청된_IP가_화이트리스트에_없는_경우_거부한다() {
        // given
        ipWhitelistValidator = new IpWhitelistValidator(true, whitelistFileLoader);
        List<String> whitelistIps = Arrays.asList("192.168.1.1", "10.0.0.0/24");
        when(whitelistFileLoader.loadWhitelistIps()).thenReturn(whitelistIps);

        // when
        boolean result = ipWhitelistValidator.isIpWhitelisted("172.16.0.1");

        // then
        assertThat(result).isFalse();
    }

    @Test
    void 화이트리스트가_비어있는_경우_거부한다() {
        // given
        ipWhitelistValidator = new IpWhitelistValidator(true, whitelistFileLoader);
        when(whitelistFileLoader.loadWhitelistIps()).thenReturn(Collections.emptyList());

        // when
        boolean result = ipWhitelistValidator.isIpWhitelisted("192.168.1.1");

        // then
        assertThat(result).isFalse();
    }

    @Test
    void 요청된_IP가_와일드카드와_일치하면_허용한다() {
        // given
        ipWhitelistValidator = new IpWhitelistValidator(true, whitelistFileLoader);
        List<String> whitelistIps = Collections.singletonList("*");
        when(whitelistFileLoader.loadWhitelistIps()).thenReturn(whitelistIps);

        // when
        boolean result = ipWhitelistValidator.isIpWhitelisted("172.16.0.1");

        // then
        assertThat(result).isTrue();
    }
}
