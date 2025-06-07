package com.library.management.mspubliccatalog.repository;

import com.library.management.mspubliccatalog.model.NewArrival;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NewArrivalRepository extends JpaRepository<NewArrival, UUID> {
    Optional<NewArrival> findByBookId(UUID bookId);
    void deleteByBookId(UUID bookId);
}