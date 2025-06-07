package com.biblio.empruntservice.entities;

import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDate;

//import org.springframework.boot.autoconfigure.SpringBootApplication;

@Entity
@Data

public class Emprunt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long utilisateurId; // lien vers microservice utilisateur
    private Long livreId;       // lien vers microservice livre
    private Long exemplaireId;
    private LocalDate dateEmprunt; 
    private LocalDate dateRetourPrevue;
    private LocalDate dateRetourReelle;

    private boolean retourne;
}
