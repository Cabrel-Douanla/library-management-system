package com.library.management.msloans.controller;

import com.library.management.msloans.dto.LoanRequest;
import com.library.management.msloans.dto.LoanResponse;
import com.library.management.msloans.enums.LoanStatus;
import com.library.management.msloans.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTHÉCAIRE')")
    public ResponseEntity<LoanResponse> createLoan(@Valid @RequestBody LoanRequest request) {
        LoanResponse createdLoan = loanService.createLoan(request);
        return new ResponseEntity<>(createdLoan, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTHÉCAIRE')") // ADMIN/BIBLIOTHÉCAIRE peuvent voir tous les emprunts
    public ResponseEntity<List<LoanResponse>> getAllLoans() {
        List<LoanResponse> loans = loanService.getAllLoans();
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTHÉCAIRE', 'ADHÉRENT')") // ADHÉRENT peut voir son propre emprunt
    public ResponseEntity<LoanResponse> getLoanById(@PathVariable UUID id) {
        // Implémenter la logique pour qu'un ADHÉRENT ne puisse voir que SES emprunts
        // Par exemple: if (hasRole("ADHÉRENT") && !getCurrentUserId().equals(loan.getUserId())) throw AccessDenied;
        LoanResponse loan = loanService.getLoanById(id);
        return ResponseEntity.ok(loan);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTHÉCAIRE', 'ADHÉRENT')") // ADHÉRENT peut voir ses propres emprunts
    public ResponseEntity<List<LoanResponse>> getLoansByUserId(@PathVariable UUID userId) {
        // Logique pour s'assurer qu'un ADHÉRENT ne peut pas voir les emprunts des autres
        // if (hasRole("ADHÉRENT") && !getCurrentUserId().equals(userId)) throw AccessDenied;
        List<LoanResponse> loans = loanService.getLoansByUserId(userId);
        return ResponseEntity.ok(loans);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTHÉCAIRE')")
    public ResponseEntity<LoanResponse> updateLoanStatus(@PathVariable UUID id, @RequestParam LoanStatus status) {
        LoanResponse updatedLoan = loanService.updateLoanStatus(id, status);
        return ResponseEntity.ok(updatedLoan);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLoan(@PathVariable UUID id) {
        loanService.deleteLoan(id);
        return ResponseEntity.noContent().build();
    }
}