package com.biblio.empruntservice.repositories;


import com.biblio.empruntservice.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
