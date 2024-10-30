package com.sky.manager;

import com.sky.api.config.DefaultFeignConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement //开启注解方式的事务管理
@ComponentScan(basePackages = {"com.sky.*","com.sky.manager.*"})
@EnableFeignClients(basePackages = "com.sky.api.client",defaultConfiguration = DefaultFeignConfig.class)
@Slf4j
public class ManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ManagerApplication.class, args);
    }
}
