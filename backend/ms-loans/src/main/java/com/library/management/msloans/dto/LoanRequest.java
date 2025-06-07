package com.library.management.msloans.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequest {
    @NotNull(message = "L'ID de l'utilisateur est requis.")
    private UUID userId;
    @NotNull(message = "L'ID du livre est requis.")
    private UUID bookId;
    private LocalDate loanDate; // Peut être auto-généré si non fourni
}