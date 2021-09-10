package com.yht.oauth2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * oauth2的鉴权服务
 *
 * @author 鱼仔
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class Oauth2SpringCloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(Oauth2SpringCloudApplication.class, args);
        System.out.println("\\_____  \\ _____   __ ___/  |_|  |__  \\_____  \\ \n" +
                " /   |   \\\\__  \\ |  |  \\   __\\  |  \\  /  ____/ \n" +
                "/    |    \\/ __ \\|  |  /|  | |   Y  \\/       \\ \n" +
                "\\_______  (____  /____/ |__| |___|  /\\_______ \\\n" +
                "        \\/     \\/                 \\/         \\/");
    }

}
