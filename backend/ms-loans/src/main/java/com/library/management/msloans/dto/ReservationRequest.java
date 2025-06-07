package com.library.management.msloans.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {
    @NotNull(message = "L'ID de l'utilisateur est requis.")
    private UUID userId;
    @NotNull(message = "L'ID du livre est requis.")
    private UUID bookId;
    private LocalDate reservationDate; // Peut être auto-généré
    @Min(value = 1, message = "La durée de validité doit être au moins de 1 jour.")
    private Integer validityDurationDays; // Durée de validité de la réservation
}