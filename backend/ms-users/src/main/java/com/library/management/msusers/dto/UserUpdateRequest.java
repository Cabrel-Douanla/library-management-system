package com.library.management.msusers.dto;

import com.library.management.msusers.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class UserUpdateRequest {
    // ID not needed in request body, typically from path variable
    // @NotBlank(message = "Nom is required") // Can be optional for partial updates
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String sexe;
    private String adresse;
    @Email(message = "Email should be valid")
    private String email; // Email could be updated, but care needed for unique constraint
    private Role role; // Role can be updated by admin
    private String etat; // "actif", "inactif", "suspendu"
    // Password update should be a separate endpoint for security reasons
}