package com.library.management.mspubliccatalog.repository;

import com.library.management.mspubliccatalog.model.Suggestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SuggestionRepository extends JpaRepository<Suggestion, UUID> {
    List<Suggestion> findByCategorieOrderByPrioriteAsc(String categorie);
    void deleteByBookId(UUID bookId); // Si un livre suggéré est supprimé
}