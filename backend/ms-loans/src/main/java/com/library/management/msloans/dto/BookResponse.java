package com.library.management.msloans.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

import com.library.management.msloans.dto.AuthorResponse;

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