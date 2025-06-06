package com.library.management.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer; // N'oubliez pas l'import
import org.springframework.cloud.netflix.eureka.EnableEurekaClient; // N'oubliez pas l'import

@SpringBootApplication
@EnableConfigServer // <-- Ajoutez cette annotation
@EnableEurekaClient // <-- Et celle-ci pour s'enregistrer auprès d'Eureka
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }

}