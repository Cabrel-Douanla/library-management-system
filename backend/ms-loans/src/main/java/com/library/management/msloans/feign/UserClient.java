package com.library.management.msloans.feign;

import com.library.management.msloans.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "ms-users", url = "http://ms-users:8080") // 'ms-users' est le nom du service Eureka/Docker
public interface UserClient {

    @GetMapping("/api/users/{id}") // Chemin exact de l'API dans ms-users
    UserResponse getUserById(@PathVariable("id") UUID id);
}