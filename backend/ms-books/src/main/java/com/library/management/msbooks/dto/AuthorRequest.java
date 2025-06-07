package com.library.management.msbooks.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorRequest {
    @NotBlank(message = "Nom de l'auteur est requis")
    private String nom;
    @NotBlank(message = "Prénom de l'auteur est requis")
    private String prenom;
    private LocalDate dateNaissance;
    private String profil;
    private String bibliographie;
}