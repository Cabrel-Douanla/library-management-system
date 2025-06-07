package com.library.management.msloans.repository;

import com.library.management.msloans.enums.ReservationStatus;
import com.library.management.msloans.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    List<Reservation> findByUserId(UUID userId);
    Optional<Reservation> findByBookIdAndStatus(UUID bookId, ReservationStatus status); // Trouver une réservation en cours pour un livre
    List<Reservation> findByBookIdOrderByReservationDateAsc(UUID bookId); // Pour gérer la file d'attente
}