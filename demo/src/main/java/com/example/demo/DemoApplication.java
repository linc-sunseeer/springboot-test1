package com.example.demo;

// 匯入 MapperScan，這個是告訴 Spring 要去哪裡掃描 MyBatis-Plus 的 mapper 介面
import org.mybatis.spring.annotation.MapperScan;
// 匯入 SpringApplication，啟動整個 Spring Boot 專案會用到
import org.springframework.boot.SpringApplication;
// 匯入 SpringBootApplication，這個是 Spring Boot 專案主入口的標準註解
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

// 這個類別就是整個後端專案的啟動入口
@SpringBootApplication
@EnableAsync
@EnableScheduling
// 這行是在說：請把 com.example.demo.mapper 底下的 mapper 都註冊進 Spring
@MapperScan("com.example.demo.mapper")
public class DemoApplication {

    // main 方法就是 Java 程式啟動時最先進來的地方
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
