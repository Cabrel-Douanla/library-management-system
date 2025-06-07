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
public class ReturnRequest {
    @NotNull(message = "L'ID de l'emprunt est requis.")
    private UUID loanId;
    private LocalDate effectiveReturnDate; // Peut être auto-généré si non fourni
    // Peut ajouter un champ pour le statut du retour (ex: "ENDOMMAGÉ", "PERDU")
}