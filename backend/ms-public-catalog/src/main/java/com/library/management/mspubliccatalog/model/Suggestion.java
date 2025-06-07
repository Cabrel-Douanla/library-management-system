package com.library.management.mspubliccatalog.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "suggestions")
public class Suggestion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "livre_id", nullable = false)
    private UUID bookId;

    @Column(length = 50)
    private String categorie; // Ex: "Populaire", "À ne pas manquer"

    private Integer priorite; // Ordre d'affichage
}