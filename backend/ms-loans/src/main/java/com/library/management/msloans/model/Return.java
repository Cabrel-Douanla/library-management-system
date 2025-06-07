package com.library.management.msloans.model;

import com.library.management.msloans.enums.ReturnStatus;
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
@Table(name = "retours")
public class Return {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "emprunt_id", nullable = false)
    private UUID loanId; // Clé logique vers Loan

    @Column(name = "date_retour", nullable = false)
    private LocalDate returnDate; // Date prévue de retour

    @Column(name = "date_effective_retour")
    private LocalDate effectiveReturnDate; // Date réelle de retour

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_retour", nullable = false, length = 20)
    private ReturnStatus status; // EN_ATTENTE, TRAITÉ, ENDOMMAGÉ, PERDU
}