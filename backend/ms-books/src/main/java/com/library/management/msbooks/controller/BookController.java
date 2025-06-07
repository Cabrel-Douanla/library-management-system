package com.library.management.msbooks.controller;

import com.library.management.msbooks.dto.BookRequest;
import com.library.management.msbooks.dto.BookResponse;
import com.library.management.msbooks.dto.BookStockUpdateRequest;
import com.library.management.msbooks.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTHÉCAIRE')")
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookRequest request) {
        BookResponse createdBook = bookService.createBook(request);
        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        List<BookResponse> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<BookResponse> getBookById(@PathVariable UUID id) {
        BookResponse book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<BookResponse> getBookByCode(@PathVariable String code) {
        BookResponse book = bookService.getBookByCode(code);
        return ResponseEntity.ok(book);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTHÉCAIRE')")
    public ResponseEntity<BookResponse> updateBook(@PathVariable UUID id, @Valid @RequestBody BookRequest request) {
        BookResponse updatedBook = bookService.updateBook(id, request);
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTHÉCAIRE')")
    public ResponseEntity<Void> deleteBook(@PathVariable UUID id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint pour la mise à jour du stock, principalement utilisé par ms-loans
    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTHÉCAIRE')") // Assurez-vous que ms-loans puisse avoir un JWT valide
    public ResponseEntity<BookResponse> updateBookStock(@PathVariable UUID id, @Valid @RequestBody BookStockUpdateRequest request) {
        BookResponse updatedBook = bookService.updateBookStock(id, request);
        return ResponseEntity.ok(updatedBook);
    }
}