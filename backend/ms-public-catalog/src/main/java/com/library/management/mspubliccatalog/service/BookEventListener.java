package com.library.management.mspubliccatalog.service;

import com.library.management.mspubliccatalog.dto.BookEvent;
import com.library.management.mspubliccatalog.model.CatalogueEntry;
import com.library.management.mspubliccatalog.model.NewArrival;
import com.library.management.mspubliccatalog.repository.CatalogueEntryRepository;
import com.library.management.mspubliccatalog.repository.NewArrivalRepository;
import com.library.management.mspubliccatalog.repository.SuggestionRepository; // Pour supprimer si le livre est supprimé
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Configuration // Pour que les @Bean des fonctions de Stream soient détectés
public class BookEventListener {

    private final CatalogueEntryRepository catalogueEntryRepository;
    private final NewArrivalRepository newArrivalRepository;
    private final SuggestionRepository suggestionRepository; // Pour la gestion des suggestions

    // Configure le consommateur pour le topic/queue "book-events"
    @Bean
    public Consumer<Message<BookEvent>> bookEventsIn0() {
        return message -> {
            BookEvent event = message.getPayload();
            System.out.println("Received book event: " + event.getEventType() + " for book ID: " + event.getBook().getId());
            processBookEvent(event);
        };
    }

    @Transactional
    public void processBookEvent(BookEvent event) {
        BookEvent.BookData bookData = event.getBook();
        String eventType = event.getEventType();

        switch (eventType) {
            case "CREATED":
                handleBookCreated(bookData);
                break;
            case "UPDATED":
                handleBookUpdated(bookData);
                break;
            case "DELETED":
                handleBookDeleted(bookData);
                break;
            case "STOCK_UPDATED":
                handleBookStockUpdated(bookData);
                break;
            default:
                System.err.println("Unknown event type: " + eventType);
        }
    }

    private void handleBookCreated(BookEvent.BookData bookData) {
        // Créer une nouvelle entrée dans le catalogue
        String authorNames = bookData.getAuthors().stream()
                .map(a -> a.getPrenom() + " " + a.getNom())
                .collect(Collectors.joining(", "));

        CatalogueEntry newEntry = CatalogueEntry.builder()
                .bookId(bookData.getId())
                .titre(bookData.getTitre())
                .auteurNom(authorNames)
                .description(bookData.getProfil())
                .isbn(bookData.getIsbn())
                .nombreExemplaires(bookData.getNombreExemplaires())
                .datePublication(LocalDate.now()) // A ajuster si la date de publication est dans BookData
                .genre("General") // Valeur par défaut, peut être amélioré
                .noteMoyenne(0.0f) // Valeur initiale
                .build();

        catalogueEntryRepository.save(newEntry);

        // Ajouter aux nouveautés
        NewArrival newArrival = NewArrival.builder()
                .bookId(bookData.getId())
                .dateAjout(LocalDate.now())
                .build();
        newArrivalRepository.save(newArrival);
    }

    private void handleBookUpdated(BookEvent.BookData bookData) {
        // Mettre à jour l'entrée existante dans le catalogue
        catalogueEntryRepository.findByBookId(bookData.getId()).ifPresent(existingEntry -> {
            String authorNames = bookData.getAuthors().stream()
                    .map(a -> a.getPrenom() + " " + a.getNom())
                    .collect(Collectors.joining(", "));

            existingEntry.setTitre(bookData.getTitre());
            existingEntry.setAuteurNom(authorNames);
            existingEntry.setDescription(bookData.getProfil());
            existingEntry.setIsbn(bookData.getIsbn());
            existingEntry.setNombreExemplaires(bookData.getNombreExemplaires());
            // Mettre à jour d'autres champs si nécessaire
            catalogueEntryRepository.save(existingEntry);
        });
    }

    private void handleBookDeleted(BookEvent.BookData bookData) {
        // Supprimer l'entrée du catalogue
        catalogueEntryRepository.deleteByBookId(bookData.getId());
        // Supprimer des nouveautés
        newArrivalRepository.deleteByBookId(bookData.getId());
        // Supprimer des suggestions (si le livre supprimé était suggéré)
        suggestionRepository.deleteByBookId(bookData.getId());
    }

    private void handleBookStockUpdated(BookEvent.BookData bookData) {
        // Mettre à jour seulement le nombre d'exemplaires dans le catalogue
        catalogueEntryRepository.findByBookId(bookData.getId()).ifPresent(existingEntry -> {
            existingEntry.setNombreExemplaires(bookData.getNombreExemplaires());
            catalogueEntryRepository.save(existingEntry);
        });
    }
}