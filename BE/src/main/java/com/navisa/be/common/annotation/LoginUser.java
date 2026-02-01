package com.navisa.be.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginUser {

    /**
     * true(기본값): 로그인 필수 (인증 실패 시 예외 발생)
     * false: 선택적 로그인 (비로그인 시 null 주입)
     */
    boolean required() default true;
}
