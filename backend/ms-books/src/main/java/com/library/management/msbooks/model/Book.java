package com.library.management.msbooks.model;

import jakarta.persistence.*;
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
@Entity
@Table(name = "livres")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false, length = 20)
    private String code; // Code interne du livre (ex: LVR001)

    @Column(nullable = false, columnDefinition = "TEXT")
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String edition;

    @Column(columnDefinition = "TEXT")
    private String profil; // Résumé ou description courte du livre

    @Column(length = 20)
    private String isbn;

    @Column(name = "nombre_exemplaires", nullable = false)
    private Integer nombreExemplaires; // Nombre d'exemplaires disponibles

    // Relation Many-to-Many avec Author via la table de jointure BookAuthor
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BookAuthor> bookAuthors;
}