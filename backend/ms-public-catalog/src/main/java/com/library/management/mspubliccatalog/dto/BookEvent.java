package com.library.management.mspubliccatalog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data; // <-- Assurez-vous que c'est là
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookEvent {
    private String eventType;
    private Instant timestamp;
    private BookData book;

    @Data // <-- CRUCIAL : Assurez-vous que BookData a @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookData {
        private UUID id;
        private String code;
        private String titre;
        private String edition;
        private String profil;
        private String isbn;
        private Integer nombreExemplaires;
        private Set<AuthorData> authors;
    }

    @Data // <-- CRUCIAL : Assurez-vous que AuthorData a @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorData {
        private UUID id;
        private String nom;
        private String prenom;
        private LocalDate dateNaissance;
        private String profil;
        private String bibliographie;
    }
}