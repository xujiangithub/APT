package com.example.annolib.anno;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE}) // 作用目标接口，类，枚举
@Retention(RetentionPolicy.CLASS) //注解会在class字节码文件中存在，但运行时无法获得
public @interface XRouter {
    String path() default ""; //参数
}
