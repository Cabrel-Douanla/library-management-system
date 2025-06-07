package com.library.management.mspubliccatalog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuggestionResponse {
    private UUID id;
    private UUID bookId;
    private String titre; // Dénormalisé pour affichage direct
    private String auteurNom; // Dénormalisé
    private String categorie;
    private Integer priorite;
}