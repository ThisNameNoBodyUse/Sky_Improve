package com.sky.order;

import com.sky.api.config.DefaultFeignConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement //开启注解方式的事务管理
@ComponentScan(basePackages = {"com.sky.*", "com.sky.order.*"})
@EnableFeignClients(basePackages = "com.sky.api.client",defaultConfiguration = DefaultFeignConfig.class)
@EnableScheduling //开启任务调度---Spring Task
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

}
