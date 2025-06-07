package com.library.management.msbooks.dto;

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
public class BookResponse {
    private UUID id;
    private String code;
    private String titre;
    private String edition;
    private String profil;
    private String isbn;
    private Integer nombreExemplaires;
    private Set<AuthorResponse> authors; // Détails des auteurs associés
}