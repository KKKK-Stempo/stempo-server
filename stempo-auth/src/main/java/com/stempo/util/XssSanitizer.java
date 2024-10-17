package com.stempo.util;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.stereotype.Component;

@Component
public class XssSanitizer {

    private final PolicyFactory policy;

    public XssSanitizer() {
        // 허용할 태그와 속성 설정 (기본적으로 형식과 링크만 허용)
        this.policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
    }

    /**
     * 입력값을 XSS로부터 안전한 값으로 변환
     *
     * @param input 사용자 입력 값
     * @return 필터링된 값
     */
    public String sanitize(String input) {
        if (input == null) {
            return null;
        }
        return this.policy.sanitize(input);
    }
}
