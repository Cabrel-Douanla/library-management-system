package com.library.management.msloans.dto;

import com.library.management.msloans.enums.ReservationStatus;
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
public class ReservationResponse {
    private UUID id;
    private UUID userId;
    private UUID bookId;
    private LocalDate reservationDate;
    private Integer validityDurationDays;
    private LocalDate effectiveLoanDate;
    private ReservationStatus status;
}