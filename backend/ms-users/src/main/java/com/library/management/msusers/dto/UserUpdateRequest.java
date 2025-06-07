package com.library.management.msusers.dto;

import com.library.management.msusers.enums.Role;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor; // <-- Ceci est correct
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    // Tous les champs sont optionnels pour permettre les mises à jour partielles
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String sexe;
    private String adresse;
    @Email(message = "Email doit être valide")
    private String email;
    private Role role; // Peut être mis à jour par l'admin
    private String etat; // Peut être mis à jour (ex: "actif", "inactif", "suspendu")
    // Le mot de passe n'est pas mis à jour ici pour des raisons de sécurité (endpoint séparé)
}