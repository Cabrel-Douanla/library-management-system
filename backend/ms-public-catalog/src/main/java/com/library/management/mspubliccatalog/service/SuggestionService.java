package com.library.management.mspubliccatalog.service;

import com.library.management.mspubliccatalog.dto.SuggestionResponse;
import com.library.management.mspubliccatalog.exception.ResourceNotFoundException;
import com.library.management.mspubliccatalog.model.Suggestion;
import com.library.management.mspubliccatalog.repository.SuggestionRepository;
import com.library.management.mspubliccatalog.repository.CatalogueEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SuggestionService {

    private final SuggestionRepository suggestionRepository;
    private final CatalogueEntryRepository catalogueEntryRepository;

    @Transactional
    public SuggestionResponse createSuggestion(UUID bookId, String category, Integer priority) {
        catalogueEntryRepository.findByBookId(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Livre non trouvé dans le catalogue avec l'ID : " + bookId));

        // Implémentez la logique pour éviter les doublons ou gérer les priorités
        Suggestion suggestion = Suggestion.builder()
                .bookId(bookId)
                .categorie(category)
                .priorite(priority)
                .build();
        Suggestion savedSuggestion = suggestionRepository.save(suggestion);
        return mapToSuggestionResponse(savedSuggestion);
    }

    public List<SuggestionResponse> getAllSuggestions() {
        return suggestionRepository.findAll().stream()
                .map(this::mapToSuggestionResponse)
                .collect(Collectors.toList());
    }

    public List<SuggestionResponse> getSuggestionsByCategory(String category) {
        return suggestionRepository.findByCategorieOrderByPrioriteAsc(category).stream()
                .map(this::mapToSuggestionResponse)
                .collect(Collectors.toList());
    }

    public SuggestionResponse getSuggestionById(UUID id) {
        Suggestion suggestion = suggestionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Suggestion non trouvée avec l'ID : " + id));
        return mapToSuggestionResponse(suggestion);
    }

    @Transactional
    public void deleteSuggestion(UUID id) {
        if (!suggestionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Suggestion non trouvée avec l'ID : " + id);
        }
        suggestionRepository.deleteById(id);
    }

    // NOUVELLE IMPLÉMENTATION DE LA MÉTHODE mapToSuggestionResponse
    private SuggestionResponse mapToSuggestionResponse(Suggestion suggestion) {
        // Tente de trouver l'entrée de catalogue correspondante
        return catalogueEntryRepository.findByBookId(suggestion.getBookId())
            .map(entry -> // Si une entrée est trouvée, map-la en SuggestionResponse
                SuggestionResponse.builder()
                    .id(suggestion.getId())
                    .bookId(suggestion.getBookId())
                    .titre(entry.getTitre()) // Accès direct aux getters de l'entrée trouvée
                    .auteurNom(entry.getAuteurNom()) // Accès direct aux getters de l'entrée trouvée
                    .categorie(suggestion.getCategorie())
                    .priorite(suggestion.getPriorite())
                    .build()
            )
            .orElseGet(() -> // Sinon, utilise les valeurs par défaut
                SuggestionResponse.builder()
                    .id(suggestion.getId())
                    .bookId(suggestion.getBookId())
                    .titre("Livre inconnu")
                    .auteurNom("Auteur inconnu")
                    .categorie(suggestion.getCategorie())
                    .priorite(suggestion.getPriorite())
                    .build()
            );
    }
}