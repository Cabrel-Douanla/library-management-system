package com.library.management.msloans.repository;

import com.library.management.msloans.enums.LoanStatus;
import com.library.management.msloans.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {
    List<Loan> findByUserId(UUID userId);
    Optional<Loan> findByBookIdAndStatus(UUID bookId, LoanStatus status); // Trouver un emprunt en cours pour un livre
}