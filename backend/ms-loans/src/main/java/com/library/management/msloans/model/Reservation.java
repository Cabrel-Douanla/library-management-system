package com.library.management.msloans.model;

import com.library.management.msloans.enums.ReservationStatus;
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
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "utilisateur_id", nullable = false)
    private UUID userId; // Clé logique vers ms-users

    @Column(name = "livre_id", nullable = false)
    private UUID bookId; // Clé logique vers ms-books

    @Column(name = "date_reservation", nullable = false)
    private LocalDate reservationDate;

    @Column(name = "duree_validite", nullable = false)
    private Integer validityDurationDays; // Durée en jours (ex: 7 jours)

    @Column(name = "date_effective_emprunt")
    private LocalDate effectiveLoanDate; // Date à laquelle la réservation est convertie en emprunt

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ReservationStatus status; // EN_ATTENTE, PRÊT_À_RETIRER, ANNULÉE, EXPIRÉE, CONVERTIE_EN_EMPRUNT
}