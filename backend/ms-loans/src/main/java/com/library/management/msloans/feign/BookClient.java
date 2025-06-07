package com.library.management.msloans.feign;

import com.library.management.msloans.dto.BookResponse;
import com.library.management.msloans.dto.BookStockUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "ms-books", url = "http://ms-books:8080") // 'ms-books' est le nom du service Eureka/Docker
public interface BookClient {

    @GetMapping("/api/books/{id}")
    BookResponse getBookById(@PathVariable("id") UUID id);

    @PatchMapping("/api/books/{id}/stock")
    BookResponse updateBookStock(@PathVariable("id") UUID id, @RequestBody BookStockUpdateRequest request);
}