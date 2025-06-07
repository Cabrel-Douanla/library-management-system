package com.biblio.empruntservice.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Retour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long empruntId;  // Référence à l'emprunt
    private LocalDate dateRetour;
    private boolean enRetard;
}
