package com.deepexi.support.feign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class StartUpApplicationTest {
    public static void main(String[] args) {
        SpringApplication.run(StartUpApplicationTest.class, args);
    }

}
