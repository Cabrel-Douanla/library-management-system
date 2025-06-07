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
public class CatalogueResponse {
    private UUID id; // ID de l'entrée de catalogue
    private UUID bookId; // ID du livre original
    private String titre;
    private String auteurNom;
    private String description;
    private String genre;
    private Float noteMoyenne;
    private LocalDate datePublication;
    private String isbn;
    private Integer nombreExemplaires;
}