package com.library.management.msbooks.service;

import com.library.management.msbooks.dto.AuthorResponse;
import com.library.management.msbooks.dto.BookRequest;
import com.library.management.msbooks.dto.BookResponse;
import com.library.management.msbooks.dto.BookStockUpdateRequest;
import com.library.management.msbooks.exception.ResourceNotFoundException;
import com.library.management.msbooks.model.Author;
import com.library.management.msbooks.model.Book;
import com.library.management.msbooks.model.BookAuthor;
import com.library.management.msbooks.repository.AuthorRepository;
import com.library.management.msbooks.repository.BookAuthorRepository;
import com.library.management.msbooks.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookAuthorRepository bookAuthorRepository;
    private final StreamBridge streamBridge; // Pour la publication d'événements

    @Transactional
    public BookResponse createBook(BookRequest request) {
        if (bookRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Un livre avec ce code existe déjà.");
        }
        if (request.getIsbn() != null && bookRepository.existsByIsbn(request.getIsbn())) {
            throw new IllegalArgumentException("Un livre avec cet ISBN existe déjà.");
        }

        Book book = Book.builder()
                .code(request.getCode())
                .titre(request.getTitre())
                .edition(request.getEdition())
                .profil(request.getProfil())
                .isbn(request.getIsbn())
                .nombreExemplaires(request.getNombreExemplaires())
                .build();

        Book savedBook = bookRepository.save(book);

        Set<BookAuthor> bookAuthors = new HashSet<>();
        if (request.getAuthorIds() != null && !request.getAuthorIds().isEmpty()) {
            request.getAuthorIds().forEach(authorId -> {
                Author author = authorRepository.findById(authorId)
                        .orElseThrow(() -> new ResourceNotFoundException("Auteur non trouvé avec l'ID : " + authorId));
                bookAuthors.add(BookAuthor.builder().book(savedBook).author(author).build());
            });
            bookAuthorRepository.saveAll(bookAuthors);
            savedBook.setBookAuthors(bookAuthors);
        }

        // Publier un événement "Livre créé"
        publishBookEvent(savedBook, "CREATED");

        return mapToBookResponse(savedBook);
    }

    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }

    public BookResponse getBookById(UUID id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livre non trouvé avec l'ID : " + id));
        return mapToBookResponse(book);
    }

    public BookResponse getBookByCode(String code) {
        Book book = bookRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Livre non trouvé avec le code : " + code));
        return mapToBookResponse(book);
    }


    @Transactional
    public BookResponse updateBook(UUID id, BookRequest request) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livre non trouvé avec l'ID : " + id));

        // Mettre à jour les champs
        if (request.getCode() != null && !request.getCode().equals(existingBook.getCode())) {
            if (bookRepository.existsByCode(request.getCode())) {
                throw new IllegalArgumentException("Un livre avec ce code existe déjà.");
            }
            existingBook.setCode(request.getCode());
        }
        if (request.getTitre() != null) existingBook.setTitre(request.getTitre());
        if (request.getEdition() != null) existingBook.setEdition(request.getEdition());
        if (request.getProfil() != null) existingBook.setProfil(request.getProfil());
        if (request.getIsbn() != null && !request.getIsbn().equals(existingBook.getIsbn())) {
            if (bookRepository.existsByIsbn(request.getIsbn())) {
                throw new IllegalArgumentException("Un livre avec cet ISBN existe déjà.");
            }
            existingBook.setIsbn(request.getIsbn());
        }
        if (request.getNombreExemplaires() != null) existingBook.setNombreExemplaires(request.getNombreExemplaires());


        // Mettre à jour les auteurs associés
        if (request.getAuthorIds() != null) {
            // Supprimer les anciennes associations
            bookAuthorRepository.deleteByBookId(id);
            existingBook.getBookAuthors().clear(); // Important pour la gestion de la relation
            // Ajouter les nouvelles associations
            Set<BookAuthor> newBookAuthors = new HashSet<>();
            request.getAuthorIds().forEach(authorId -> {
                Author author = authorRepository.findById(authorId)
                        .orElseThrow(() -> new ResourceNotFoundException("Auteur non trouvé avec l'ID : " + authorId));
                newBookAuthors.add(BookAuthor.builder().book(existingBook).author(author).build());
            });
            bookAuthorRepository.saveAll(newBookAuthors);
            existingBook.setBookAuthors(newBookAuthors);
        }

        Book updatedBook = bookRepository.save(existingBook);

        // Publier un événement "Livre mis à jour"
        publishBookEvent(updatedBook, "UPDATED");

        return mapToBookResponse(updatedBook);
    }

    @Transactional
    public void deleteBook(UUID id) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livre non trouvé avec l'ID : " + id));

        bookRepository.delete(existingBook); // La cascade supprimera les BookAuthor

        // Publier un événement "Livre supprimé"
        publishBookEvent(existingBook, "DELETED");
    }

    @Transactional
    public BookResponse updateBookStock(UUID id, BookStockUpdateRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livre non trouvé avec l'ID : " + id));

        int newStock = book.getNombreExemplaires() + request.getChange();
        if (newStock < 0) {
            throw new IllegalArgumentException("Le nombre d'exemplaires ne peut pas être négatif.");
        }
        book.setNombreExemplaires(newStock);
        Book updatedBook = bookRepository.save(book);

        // Publier un événement "Stock de livre mis à jour"
        publishBookEvent(updatedBook, "STOCK_UPDATED");

        return mapToBookResponse(updatedBook);
    }

    // Helper pour mapper l'entité Book au DTO BookResponse
    private BookResponse mapToBookResponse(Book book) {
        Set<AuthorResponse> authorResponses = book.getBookAuthors().stream()
                .map(ba -> AuthorResponse.builder()
                        .id(ba.getAuthor().getId())
                        .nom(ba.getAuthor().getNom())
                        .prenom(ba.getAuthor().getPrenom())
                        .dateNaissance(ba.getAuthor().getDateNaissance())
                        .profil(ba.getAuthor().getProfil())
                        .bibliographie(ba.getAuthor().getBibliographie())
                        .build())
                .collect(Collectors.toSet());

        return BookResponse.builder()
                .id(book.getId())
                .code(book.getCode())
                .titre(book.getTitre())
                .edition(book.getEdition())
                .profil(book.getProfil())
                .isbn(book.getIsbn())
                .nombreExemplaires(book.getNombreExemplaires())
                .authors(authorResponses)
                .build();
    }

    // Méthode pour publier un événement de livre
    private void publishBookEvent(Book book, String eventType) {
        // Créer une représentation de l'événement (peut être un DTO dédié pour les événements)
        // Pour l'exemple, utilisons un Map simple
        // Assurez-vous que les données envoyées sont suffisantes pour le consommateur (ms-public-catalog)
        // pour mettre à jour sa vue dénormalisée.
        // On peut envoyer une version simplifiée du BookResponse si on veut.
        // Ici, envoyons le BookResponse complet pour flexibilité.
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", eventType);
        event.put("timestamp", Instant.now());
        event.put("book", mapToBookResponse(book)); // Envoyez le DTO BookResponse

        // Le nom du binding est 'book-events-out-0' dans application.yml
        streamBridge.send("book-events-out-0", event);
        System.out.println("Published book event: " + eventType + " for book ID: " + book.getId());
    }
}