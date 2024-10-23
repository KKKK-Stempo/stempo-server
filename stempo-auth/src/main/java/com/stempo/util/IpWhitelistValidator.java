package com.stempo.util;

import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IpWhitelistValidator {

    private final boolean whitelistEnabled;
    private final WhitelistFileLoader whitelistFileLoader;

    protected IpWhitelistValidator(
            @Value("${security.whitelist.enabled}") boolean whitelistEnabled,
            WhitelistFileLoader whitelistFileLoader
    ) {
        this.whitelistEnabled = whitelistEnabled;
        this.whitelistFileLoader = whitelistFileLoader;
    }

    /**
     * 요청된 IP가 화이트리스트에 포함되는지 확인합니다.
     *
     * @param ipAddress 확인하려는 IP 주소
     * @return IP가 화이트리스트에 포함되면 true, 그렇지 않으면 false
     */
    public boolean isIpWhitelisted(String ipAddress) {
        if (!whitelistEnabled) {
            return true;
        }

        List<String> whitelistIps = whitelistFileLoader.loadWhitelistIps();

        if (whitelistIps.isEmpty()) {
            return false;
        }

        return whitelistIps.stream()
                .filter(Objects::nonNull)
                .anyMatch(ip -> "*".equals(ip) || IpAddressUtils.isIpInRange(ipAddress, ip));
    }
}
