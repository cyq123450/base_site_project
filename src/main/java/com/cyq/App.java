package com.cyq;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 主启动类
 */
@SpringBootApplication
@MapperScan(basePackages = "com.cyq.mapper")
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class);
    }

}
