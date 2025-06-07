package com.biblio.empruntservice.repositories;

import com.biblio.empruntservice.entities.Retour;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RetourRepository extends JpaRepository<Retour, Long> {
}
