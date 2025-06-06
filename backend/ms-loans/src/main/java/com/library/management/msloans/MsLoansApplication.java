package com.library.management.msloans;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients; // Pour les appels synchrones inter-services

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients // <-- Ajoutez cette annotation si vous utilisez OpenFeign
public class MsLoansApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsLoansApplication.class, args);
    }

}