package com.stempo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SuccessApiResponse {
    String description() default "OK";
    String data() default "";
    Class<?> dataType() default Void.class;
    String dataDescription() default "";
}
