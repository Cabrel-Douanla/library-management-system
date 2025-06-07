package com.library.management.msloans.controller;

import com.library.management.msloans.dto.LoanResponse;
import com.library.management.msloans.dto.ReservationRequest;
import com.library.management.msloans.dto.ReservationResponse;
import com.library.management.msloans.enums.ReservationStatus;
import com.library.management.msloans.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTHÉCAIRE', 'ADHÉRENT')")
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request) {
        ReservationResponse createdReservation = reservationService.createReservation(request);
        return new ResponseEntity<>(createdReservation, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTHÉCAIRE')")
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        List<ReservationResponse> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTHÉCAIRE', 'ADHÉRENT')")
    public ResponseEntity<ReservationResponse> getReservationById(@PathVariable UUID id) {
        ReservationResponse reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(reservation);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTHÉCAIRE', 'ADHÉRENT')")
    public ResponseEntity<List<ReservationResponse>> getReservationsByUserId(@PathVariable UUID userId) {
        // Implémentez ici la logique pour qu'un ADHÉRENT ne puisse voir que SES réservations
        List<ReservationResponse> reservations = reservationService.getReservationsByUserId(userId);
        return ResponseEntity.ok(reservations);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTHÉCAIRE')")
    public ResponseEntity<ReservationResponse> updateReservationStatus(@PathVariable UUID id, @RequestParam ReservationStatus status) {
        ReservationResponse updatedReservation = reservationService.updateReservationStatus(id, status);
        return ResponseEntity.ok(updatedReservation);
    }

    @PatchMapping("/{id}/convert-to-loan")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTHÉCAIRE')")
    public ResponseEntity<LoanResponse> convertReservationToLoan(@PathVariable UUID id) {
        LoanResponse loan = reservationService.convertReservationToLoan(id);
        return ResponseEntity.ok(loan);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTHÉCAIRE')") // Un ADHÉRENT pourrait annuler sa propre réservation
    public ResponseEntity<Void> deleteReservation(@PathVariable UUID id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}