package com.library.management.msbooks.repository;

import com.library.management.msbooks.model.BookAuthor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookAuthorRepository extends JpaRepository<BookAuthor, UUID> {
    void deleteByBookId(UUID bookId); // Pour gérer la suppression des associations
    List<BookAuthor> findByBookId(UUID bookId);
}