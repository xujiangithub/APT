package com.example.annolib.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by xj on 2019/6/8.
 */

@Retention(RetentionPolicy.RUNTIME)  //RetentionPolicy.RUNTIME表示运行时注解
@Target(ElementType.TYPE) //表明该注解是作用于类上面
public @interface InterfaceCreateAnno {
}