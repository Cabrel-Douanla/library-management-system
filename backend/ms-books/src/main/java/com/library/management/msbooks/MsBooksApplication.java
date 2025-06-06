package com.library.management.msbooks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsBooksApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsBooksApplication.class, args);
    }

}