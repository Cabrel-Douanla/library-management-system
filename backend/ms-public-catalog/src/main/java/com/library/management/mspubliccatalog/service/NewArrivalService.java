package com.library.management.mspubliccatalog.service;

import com.library.management.mspubliccatalog.dto.NewArrivalResponse;
import com.library.management.mspubliccatalog.exception.ResourceNotFoundException;
import com.library.management.mspubliccatalog.model.NewArrival;
import com.library.management.mspubliccatalog.repository.NewArrivalRepository;
import com.library.management.mspubliccatalog.repository.CatalogueEntryRepository; // Pour récupérer titre et auteur
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewArrivalService {

    private final NewArrivalRepository newArrivalRepository;
    private final CatalogueEntryRepository catalogueEntryRepository; // Pour enrichir la réponse

    public List<NewArrivalResponse> getAllNewArrivals() {
        return newArrivalRepository.findAll().stream()
                .map(this::mapToNewArrivalResponse)
                .collect(Collectors.toList());
    }

    public NewArrivalResponse getNewArrivalById(UUID id) {
        NewArrival newArrival = newArrivalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nouveauté non trouvée avec l'ID : " + id));
        return mapToNewArrivalResponse(newArrival);
    }

    // Méthode pour ajouter manuellement une nouveauté (si besoin, ou laisser l'événement faire tout le travail)
    // @Transactional
    // public NewArrivalResponse addNewArrival(UUID bookId) {
    //     // Logic to check if book exists and add to new arrivals
    // }

    private NewArrivalResponse mapToNewArrivalResponse(NewArrival newArrival) {
        // Tente de trouver l'entrée de catalogue correspondante
        return catalogueEntryRepository.findByBookId(newArrival.getBookId())
            .map(entry -> // Si une entrée est trouvée, map-la en NewArrivalResponse
                NewArrivalResponse.builder()
                    .id(newArrival.getId())
                    .bookId(newArrival.getBookId())
                    .titre(entry.getTitre())
                    .auteurNom(entry.getAuteurNom())
                    .dateAjout(newArrival.getDateAjout())
                    .build()
            )
            .orElseGet(() -> // Sinon, utilise les valeurs par défaut
                NewArrivalResponse.builder()
                    .id(newArrival.getId())
                    .bookId(newArrival.getBookId())
                    .titre("Livre inconnu")
                    .auteurNom("Auteur inconnu")
                    .dateAjout(newArrival.getDateAjout())
                    .build()
            );
    }
    
}