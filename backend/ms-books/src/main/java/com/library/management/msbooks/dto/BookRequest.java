package com.library.management.msbooks.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookRequest {
    @NotBlank(message = "Le code du livre est requis")
    private String code;
    @NotBlank(message = "Le titre du livre est requis")
    private String titre;
    private String edition;
    private String profil;
    private String isbn;
    @NotNull(message = "Le nombre d'exemplaires est requis")
    @Min(value = 0, message = "Le nombre d'exemplaires ne peut pas être négatif")
    private Integer nombreExemplaires;
    private Set<UUID> authorIds; // IDs des auteurs associés
}