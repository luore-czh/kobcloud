package com.kob.matchingsystem;


import com.kob.matchingsystem.service.impl.MatchingServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class MatchSystemApplication {
    public static void main(String[] args) {
        MatchingServiceImpl.matchingpool.start(); //启动匹配线程
        SpringApplication.run(MatchSystemApplication.class, args);
    }
}