package com.example.log.annotation;

import java.lang.annotation.*;

/**
 * 日志注解类
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XLog {
    /**
     * 操作模块
     */
    String operaModule() default "";

    /**
     * 操作类型
     */
    String operaType() default "";
}
