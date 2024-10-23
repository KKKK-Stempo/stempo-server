package com.stempo.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class IpAddressUtilsTest {

    @Test
    void 주어진_IP가_서브넷_내에_있을_경우_일치하는지_확인한다() {
        // Given
        String ipAddress = "192.168.1.10";
        String subnet = "192.168.1.0/24";

        // When
        boolean result = IpAddressUtils.isIpInRange(ipAddress, subnet);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void 주어진_IP가_서브넷_내에_없을_경우_일치하지_않는지_확인한다() {
        // Given
        String ipAddress = "192.168.2.10";
        String subnet = "192.168.1.0/24";

        // When
        boolean result = IpAddressUtils.isIpInRange(ipAddress, subnet);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void 주어진_IP가_단일_IP와_일치하는지_확인한다() {
        // Given
        String ipAddress = "192.168.1.10";
        String singleIp = "192.168.1.10";

        // When
        boolean result = IpAddressUtils.isIpInRange(ipAddress, singleIp);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void 주어진_IP가_단일_IP와_일치하지_않는지_확인한다() {
        // Given
        String ipAddress = "192.168.1.11";
        String singleIp = "192.168.1.10";

        // When
        boolean result = IpAddressUtils.isIpInRange(ipAddress, singleIp);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void 잘못된_CIDR_주소가_주어졌을_경우_예외를_처리한다() {
        // Given
        String ipAddress = "192.168.1.10";
        String invalidCidr = "192.168.1.0/33";

        // When
        boolean result = IpAddressUtils.isIpInRange(ipAddress, invalidCidr);

        // Then
        assertThat(result).isFalse();
    }
}
