package com.library.management.msloans.dto;

import com.library.management.msloans.enums.LoanStatus;
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
public class LoanResponse {
    private UUID id;
    private UUID userId;
    private UUID bookId;
    private LocalDate loanDate;
    private LoanStatus status;
    // Optionnellement, vous pouvez ajouter des informations du livre et de l'utilisateur ici,
    // mais cela complexifie le DTO et peut nécessiter des appels Feign supplémentaires.
    // Pour un DTO de réponse, souvent, les IDs suffisent et le frontend agrège.
}