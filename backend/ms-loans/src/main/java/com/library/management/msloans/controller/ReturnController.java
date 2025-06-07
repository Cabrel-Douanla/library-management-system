package com.library.management.msloans.controller;

import com.library.management.msloans.dto.ReturnRequest;
import com.library.management.msloans.dto.ReturnResponse;
import com.library.management.msloans.enums.ReturnStatus;
import com.library.management.msloans.service.ReturnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/returns")
@RequiredArgsConstructor
public class ReturnController {

    private final ReturnService returnService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTHÉCAIRE')")
    public ResponseEntity<ReturnResponse> recordReturn(@Valid @RequestBody ReturnRequest request) {
        ReturnResponse recordedReturn = returnService.recordReturn(request);
        return new ResponseEntity<>(recordedReturn, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTHÉCAIRE')")
    public ResponseEntity<List<ReturnResponse>> getAllReturns() {
        List<ReturnResponse> returns = returnService.getAllReturns();
        return ResponseEntity.ok(returns);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTHÉCAIRE')")
    public ResponseEntity<ReturnResponse> getReturnById(@PathVariable UUID id) {
        ReturnResponse ret = returnService.getReturnById(id);
        return ResponseEntity.ok(ret);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTHÉCAIRE')")
    public ResponseEntity<ReturnResponse> updateReturnStatus(@PathVariable UUID id, @RequestParam ReturnStatus status) {
        ReturnResponse updatedReturn = returnService.updateReturnStatus(id, status);
        return ResponseEntity.ok(updatedReturn);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReturn(@PathVariable UUID id) {
        returnService.deleteReturn(id);
        return ResponseEntity.noContent().build();
    }
}