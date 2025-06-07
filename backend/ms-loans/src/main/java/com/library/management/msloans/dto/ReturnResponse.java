package com.library.management.msloans.dto;

import com.library.management.msloans.enums.ReturnStatus;
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
public class ReturnResponse {
    private UUID id;
    private UUID loanId;
    private LocalDate returnDate;
    private LocalDate effectiveReturnDate;
    private ReturnStatus status;
}