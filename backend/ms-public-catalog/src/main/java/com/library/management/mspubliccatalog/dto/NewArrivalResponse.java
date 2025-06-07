package com.library.management.mspubliccatalog.dto;

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
public class NewArrivalResponse {
    private UUID id;
    private UUID bookId;
    private String titre; // Dénormalisé pour affichage direct
    private String auteurNom; // Dénormalisé
    private LocalDate dateAjout;
}