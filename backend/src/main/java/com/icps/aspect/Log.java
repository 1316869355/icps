package com.icps.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义日志注解，用于标记需要打印日志的方法
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
    /**
     * 日志描述
     */
    String value() default "";
    
    /**
     * 是否打印请求参数
     */
    boolean printParams() default true;
    
    /**
     * 是否打印返回结果
     */
    boolean printResult() default true;
    
    /**
     * 是否打印执行时间
     */
    boolean printExecutionTime() default true;
}