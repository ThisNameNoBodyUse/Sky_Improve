package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于表示某个方法需要进行公共字段自动填充处理
 */

@Target(ElementType.METHOD) //指明注解只能应用在方法上面
@Retention(RetentionPolicy.RUNTIME) //注解在运行时可用，通过反射可以获取到
public @interface AutoFill {
    //数据库操作类型 ： UPDATE INSERT
    OperationType value();
}
