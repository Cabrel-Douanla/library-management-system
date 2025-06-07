package com.library.management.msbooks.repository;

import com.library.management.msbooks.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {
    Optional<Book> findByCode(String code);
    boolean existsByCode(String code);
    boolean existsByIsbn(String isbn);
}