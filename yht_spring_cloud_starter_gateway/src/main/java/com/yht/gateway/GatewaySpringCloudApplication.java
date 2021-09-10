package com.yht.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author 鱼仔
 * @date 2021/4/20 10:38 上午
 * 概要
 */
@EnableDiscoveryClient
@SpringBootApplication
public class GatewaySpringCloudApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewaySpringCloudApplication.class, args);
    }
}
