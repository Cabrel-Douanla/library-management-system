package com.library.management.mspubliccatalog.service;

import com.library.management.mspubliccatalog.dto.CatalogueResponse;
import com.library.management.mspubliccatalog.exception.ResourceNotFoundException;
import com.library.management.mspubliccatalog.model.CatalogueEntry;
import com.library.management.mspubliccatalog.repository.CatalogueEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogueService {

    private final CatalogueEntryRepository catalogueEntryRepository;

    public List<CatalogueResponse> getAllCatalogueEntries() {
        return catalogueEntryRepository.findAll().stream()
                .map(this::mapToCatalogueResponse)
                .collect(Collectors.toList());
    }

    public CatalogueResponse getCatalogueEntryById(UUID id) {
        CatalogueEntry entry = catalogueEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrée de catalogue non trouvée avec l'ID : " + id));
        return mapToCatalogueResponse(entry);
    }

    // Ajouter des méthodes de recherche (par titre, auteur, genre)
    // public List<CatalogueResponse> searchBooksByTitle(String title) { ... }
    // public List<CatalogueResponse> searchBooksByAuthor(String authorName) { ... }

    private CatalogueResponse mapToCatalogueResponse(CatalogueEntry entry) {
        return CatalogueResponse.builder()
                .id(entry.getId())
                .bookId(entry.getBookId())
                .titre(entry.getTitre())
                .auteurNom(entry.getAuteurNom())
                .description(entry.getDescription())
                .genre(entry.getGenre())
                .noteMoyenne(entry.getNoteMoyenne())
                .datePublication(entry.getDatePublication())
                .isbn(entry.getIsbn())
                .nombreExemplaires(entry.getNombreExemplaires())
                .build();
    }
}