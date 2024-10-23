package com.stempo.util;

import com.stempo.config.WhitelistProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class WhitelistPathMatcher implements InitializingBean {

    private static String[] actuatorPatterns;
    private static String[] apiDocsPatterns;
    private static String[] whitelistPatterns;

    private final WhitelistProperties whitelistProperties;

    public WhitelistPathMatcher(WhitelistProperties whitelistProperties) {
        this.whitelistProperties = whitelistProperties;
    }

    @Override
    public void afterPropertiesSet() {
        actuatorPatterns = whitelistProperties.getPatterns().getActuator();
        apiDocsPatterns = whitelistProperties.getPatterns().getApiDocs();
        whitelistPatterns = whitelistProperties.getPatterns().getWhitelistPatterns();
    }

    public static boolean isApiDocsRequest(String path) {
        return isPatternMatch(path, apiDocsPatterns);
    }

    public static boolean isActuatorRequest(String path) {
        return isPatternMatch(path, actuatorPatterns);
    }

    public static boolean isWhitelistRequest(String path) {
        return isPatternMatch(path, whitelistPatterns);
    }

    public static boolean isApiDocsIndexEndpoint(String path) {
        return apiDocsPatterns[2].equals(path);
    }

    protected static boolean isPatternMatch(String path, String[] patterns) {
        for (String pattern : patterns) {
            if (Pattern.compile(pattern).matcher(path).find()) {
                return true;
            }
        }
        return false;
    }
}
