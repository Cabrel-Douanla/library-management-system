package com.library.management.mspubliccatalog.repository;

import com.library.management.mspubliccatalog.model.CatalogueEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CatalogueEntryRepository extends JpaRepository<CatalogueEntry, UUID> {
    Optional<CatalogueEntry> findByBookId(UUID bookId); // Utile pour la synchronisation
    void deleteByBookId(UUID bookId); // Utile pour la suppression
}