package com.library.management.mspubliccatalog.model;

import jakarta.persistence.*;
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
@Entity
@Table(name = "catalogue")
public class CatalogueEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "livre_id", unique = true, nullable = false)
    private UUID bookId; // L'ID du livre original de ms-books

    @Column(nullable = false, columnDefinition = "TEXT")
    private String titre;

    @Column(name = "auteur_nom", columnDefinition = "TEXT")
    private String auteurNom; // Dénormalisé: Nom complet de l'auteur (ou liste d'auteurs)

    @Column(columnDefinition = "TEXT")
    private String description; // Profil du livre de ms-books

    @Column(length = 50)
    private String genre; // Nouveau champ pour le catalogue public

    @Column(name = "note_moyenne")
    private Float noteMoyenne; // Nouvelle métrique, à implémenter plus tard si un service de notation existe

    @Column(name = "date_publication")
    private LocalDate datePublication; // Date de publication du livre

    private String isbn;
    private Integer nombreExemplaires; // Nombre d'exemplaires disponibles (peut être mis à jour par événements)
}