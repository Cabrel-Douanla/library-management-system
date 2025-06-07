package com.library.management.mspubliccatalog.controller;

import com.library.management.mspubliccatalog.dto.CatalogueResponse;
import com.library.management.mspubliccatalog.dto.NewArrivalResponse;
import com.library.management.mspubliccatalog.dto.SuggestionResponse;
import com.library.management.mspubliccatalog.service.CatalogueService;
import com.library.management.mspubliccatalog.service.NewArrivalService;
import com.library.management.mspubliccatalog.service.SuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicCatalogController {

    private final CatalogueService catalogueService;
    private final NewArrivalService newArrivalService;
    private final SuggestionService suggestionService;

    // --- Catalogue Endpoints ---
    @GetMapping("/catalogue")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<CatalogueResponse>> getAllCatalogueEntries() {
        List<CatalogueResponse> entries = catalogueService.getAllCatalogueEntries();
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/catalogue/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<CatalogueResponse> getCatalogueEntryById(@PathVariable UUID id) {
        CatalogueResponse entry = catalogueService.getCatalogueEntryById(id);
        return ResponseEntity.ok(entry);
    }

    // --- New Arrivals Endpoints ---
    @GetMapping("/nouveautes")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<NewArrivalResponse>> getAllNewArrivals() {
        List<NewArrivalResponse> newArrivals = newArrivalService.getAllNewArrivals();
        return ResponseEntity.ok(newArrivals);
    }

    // --- Suggestions Endpoints ---
    @GetMapping("/suggestions")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<SuggestionResponse>> getAllSuggestions(@RequestParam(required = false) String category) {
        List<SuggestionResponse> suggestions;
        if (category != null && !category.isEmpty()) {
            suggestions = suggestionService.getSuggestionsByCategory(category);
        } else {
            suggestions = suggestionService.getAllSuggestions();
        }
        return ResponseEntity.ok(suggestions);
    }

    // Endpoint for ADMIN/BIBLIOTHECAIRE to manually add a suggestion
    @PostMapping("/suggestions")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTHÉCAIRE')")
    public ResponseEntity<SuggestionResponse> createSuggestion(
            @RequestParam UUID bookId,
            @RequestParam String category,
            @RequestParam Integer priority) {
        SuggestionResponse createdSuggestion = suggestionService.createSuggestion(bookId, category, priority);
        return new ResponseEntity<>(createdSuggestion, HttpStatus.CREATED);
    }

    // Endpoint for ADMIN/BIBLIOTHECAIRE to delete a suggestion
    @DeleteMapping("/suggestions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTHÉCAIRE')")
    public ResponseEntity<Void> deleteSuggestion(@PathVariable UUID id) {
        suggestionService.deleteSuggestion(id);
        return ResponseEntity.noContent().build();
    }
}