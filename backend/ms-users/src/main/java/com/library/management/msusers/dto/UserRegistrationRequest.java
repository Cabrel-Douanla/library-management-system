package com.library.management.msusers.dto;

import com.library.management.msusers.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequest {
    // Le champ 'code' peut être généré ou fourni par l'admin
    private String code; // Peut être null lors de l'inscription simple

    @NotBlank(message = "Nom est requis")
    private String nom;

    @NotBlank(message = "Prénom est requis")
    private String prenom;

    private LocalDate dateNaissance;
    private String sexe;
    private String adresse;

    @NotBlank(message = "Email est requis")
    @Email(message = "Email doit être valide")
    private String email;

    @NotBlank(message = "Mot de passe est requis")
    @Size(min = 6, message = "Mot de passe doit contenir au moins 6 caractères")
    private String password;

    @NotNull(message = "Rôle est requis")
    private Role role;
}