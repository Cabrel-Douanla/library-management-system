package com.library.management.msloans.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookStockUpdateRequest {
    @NotNull(message = "La modification de stock est requise")
    private Integer change; // Peut être positif (ajout) ou négatif (retrait)
}