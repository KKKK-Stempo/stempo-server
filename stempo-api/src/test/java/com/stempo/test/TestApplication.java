package com.stempo.test;

import com.stempo.exception.GlobalExceptionHandler;
import com.stempo.test.config.TestSecurityConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = {"com.stempo.controller", "com.stempo.exception"})
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
public class TestApplication {
}
