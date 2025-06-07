package com.library.management.msloans.service;

import com.library.management.msloans.dto.BookResponse;
import com.library.management.msloans.dto.LoanRequest;
import com.library.management.msloans.dto.LoanResponse;
import com.library.management.msloans.dto.ReservationRequest;
import com.library.management.msloans.dto.ReservationResponse;
import com.library.management.msloans.enums.ReservationStatus;
import com.library.management.msloans.exception.ResourceNotFoundException;
import com.library.management.msloans.feign.BookClient;
import com.library.management.msloans.feign.UserClient;
import com.library.management.msloans.model.Reservation;
import com.library.management.msloans.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserClient userClient;
    private final BookClient bookClient;
    private final LoanService loanService; // Pour convertir une réservation en emprunt

    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        // 1. Valider l'existence de l'utilisateur
        try {
            userClient.getUserById(request.getUserId());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Utilisateur non trouvé avec l'ID : " + request.getUserId());
        }

        // 2. Valider l'existence du livre
        try {
            bookClient.getBookById(request.getBookId());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Livre non trouvé avec l'ID : " + request.getBookId());
        }

        // 3. Vérifier si l'utilisateur n'a pas déjà réservé ce livre
        if (reservationRepository.findByBookIdAndStatus(request.getBookId(), ReservationStatus.EN_ATTENTE).isPresent()) {
             // On peut rendre cela plus sophistiqué si un livre peut avoir plusieurs réservations en file d'attente
             // Pour l'instant, on suppose une seule réservation "en attente" par livre pour simplifier.
             // Si on veut une file d'attente, il faut gérer les priorités.
            throw new IllegalArgumentException("Ce livre est déjà réservé par un autre utilisateur.");
        }

        Reservation reservation = Reservation.builder()
                .userId(request.getUserId())
                .bookId(request.getBookId())
                .reservationDate(request.getReservationDate() != null ? request.getReservationDate() : LocalDate.now())
                .validityDurationDays(request.getValidityDurationDays() != null ? request.getValidityDurationDays() : 7) // Durée par défaut
                .status(ReservationStatus.EN_ATTENTE)
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);
        return mapToReservationResponse(savedReservation);
    }

    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::mapToReservationResponse)
                .collect(Collectors.toList());
    }

    public ReservationResponse getReservationById(UUID id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée avec l'ID : " + id));
        return mapToReservationResponse(reservation);
    }

    public List<ReservationResponse> getReservationsByUserId(UUID userId) {
        return reservationRepository.findByUserId(userId).stream()
                .map(this::mapToReservationResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservationResponse updateReservationStatus(UUID id, ReservationStatus newStatus) {
        Reservation existingReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée avec l'ID : " + id));

        // Logique de transition de statut
        if (newStatus == ReservationStatus.CONVERTIE_EN_EMPRUNT) {
            throw new IllegalArgumentException("Utilisez l'endpoint de conversion pour marquer une réservation comme empruntée.");
        }

        existingReservation.setStatus(newStatus);
        Reservation updatedReservation = reservationRepository.save(existingReservation);
        return mapToReservationResponse(updatedReservation);
    }

    @Transactional
    public LoanResponse convertReservationToLoan(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée avec l'ID : " + reservationId));

        if (reservation.getStatus() != ReservationStatus.EN_ATTENTE && reservation.getStatus() != ReservationStatus.PRÊT_À_RETIRER) {
            throw new IllegalArgumentException("Seules les réservations 'EN_ATTENTE' ou 'PRÊT_À_RETIRER' peuvent être converties.");
        }

        // Vérifier la disponibilité du livre avant de convertir (double-check si nécessaire)
        BookResponse book = bookClient.getBookById(reservation.getBookId());
        if (book.getNombreExemplaires() <= 0) {
            throw new IllegalArgumentException("Le livre n'est plus disponible pour convertir la réservation.");
        }

        // Créer un emprunt via le LoanService
        LoanRequest loanRequest = LoanRequest.builder()
                .userId(reservation.getUserId())
                .bookId(reservation.getBookId())
                .loanDate(LocalDate.now())
                .build();
        LoanResponse newLoan = loanService.createLoan(loanRequest);

        // Mettre à jour le statut de la réservation
        reservation.setStatus(ReservationStatus.CONVERTIE_EN_EMPRUNT);
        reservation.setEffectiveLoanDate(newLoan.getLoanDate()); // Enregistre la date effective de l'emprunt
        reservationRepository.save(reservation);

        return newLoan;
    }

    @Transactional
    public void deleteReservation(UUID id) {
        if (!reservationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Réservation non trouvée avec l'ID : " + id);
        }
        reservationRepository.deleteById(id);
    }

    private ReservationResponse mapToReservationResponse(Reservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .userId(reservation.getUserId())
                .bookId(reservation.getBookId())
                .reservationDate(reservation.getReservationDate())
                .validityDurationDays(reservation.getValidityDurationDays())
                .effectiveLoanDate(reservation.getEffectiveLoanDate())
                .status(reservation.getStatus())
                .build();
    }
}