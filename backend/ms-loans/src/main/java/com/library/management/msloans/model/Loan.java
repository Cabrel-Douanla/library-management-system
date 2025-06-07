package com.library.management.msloans.model;

import com.library.management.msloans.enums.LoanStatus;
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
@Table(name = "emprunts")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "utilisateur_id", nullable = false)
    private UUID userId; // Clé logique vers ms-users

    @Column(name = "livre_id", nullable = false)
    private UUID bookId; // Clé logique vers ms-books

    @Column(name = "date_emprunt", nullable = false)
    private LocalDate loanDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_emprunt", nullable = false, length = 20)
    private LoanStatus status; // EN_COURS, TERMINÉ, ANNULÉ, EN_RETARD
}