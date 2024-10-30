package com.sky.product;

import com.sky.api.config.DefaultFeignConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement //开启注解方式的事务管理
@ComponentScan(basePackages = {"com.sky.*", "com.sky.product.*"})
@EnableFeignClients(basePackages = "com.sky.api.client",defaultConfiguration = DefaultFeignConfig.class)
@EnableCaching //启用缓存
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }

}
