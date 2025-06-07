package com.library.management.msloans.service;

import com.library.management.msloans.dto.BookStockUpdateRequest;
import com.library.management.msloans.dto.ReturnRequest;
import com.library.management.msloans.dto.ReturnResponse;
import com.library.management.msloans.enums.LoanStatus;
import com.library.management.msloans.enums.ReturnStatus;
import com.library.management.msloans.exception.ResourceNotFoundException;
import com.library.management.msloans.feign.BookClient;
import com.library.management.msloans.model.Loan;
import com.library.management.msloans.model.Return;
import com.library.management.msloans.repository.LoanRepository;
import com.library.management.msloans.repository.ReturnRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReturnService {

    private final ReturnRepository returnRepository;
    private final LoanRepository loanRepository;
    private final BookClient bookClient;

    @Transactional
    public ReturnResponse recordReturn(ReturnRequest request) {
        Loan loan = loanRepository.findById(request.getLoanId())
                .orElseThrow(() -> new ResourceNotFoundException("Emprunt non trouvé avec l'ID : " + request.getLoanId()));

        if (loan.getStatus() == LoanStatus.TERMINÉ) {
            throw new IllegalArgumentException("Cet emprunt a déjà été marqué comme terminé.");
        }

        // Mettre à jour le statut de l'emprunt à TERMINÉ
        loan.setStatus(LoanStatus.TERMINÉ);
        loanRepository.save(loan);

        // Incrémenter le stock du livre dans ms-books
        try {
            bookClient.updateBookStock(loan.getBookId(), BookStockUpdateRequest.builder().change(1).build());
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'incrémentation du stock du livre : " + e.getMessage());
        }

        // Créer l'enregistrement de retour
        Return newReturn = Return.builder()
                .loanId(loan.getId())
                .returnDate(loan.getLoanDate().plusWeeks(3)) // Date de retour prévue (exemple)
                .effectiveReturnDate(request.getEffectiveReturnDate() != null ? request.getEffectiveReturnDate() : LocalDate.now())
                .status(ReturnStatus.TRAITÉ) // Peut être ajusté selon la demande (ex: ENDOMMAGÉ)
                .build();

        Return savedReturn = returnRepository.save(newReturn);
        return mapToReturnResponse(savedReturn);
    }

    public List<ReturnResponse> getAllReturns() {
        return returnRepository.findAll().stream()
                .map(this::mapToReturnResponse)
                .collect(Collectors.toList());
    }

    public ReturnResponse getReturnById(UUID id) {
        Return ret = returnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Retour non trouvé avec l'ID : " + id));
        return mapToReturnResponse(ret);
    }

    // Méthode pour mettre à jour le statut d'un retour (ex: marquer comme ENDOMMAGÉ)
    @Transactional
    public ReturnResponse updateReturnStatus(UUID returnId, ReturnStatus newStatus) {
        Return existingReturn = returnRepository.findById(returnId)
                .orElseThrow(() -> new ResourceNotFoundException("Retour non trouvé avec l'ID : " + returnId));

        // Validation de transition de statut si nécessaire
        existingReturn.setStatus(newStatus);
        Return updatedReturn = returnRepository.save(existingReturn);
        return mapToReturnResponse(updatedReturn);
    }

    @Transactional
    public void deleteReturn(UUID id) {
        if (!returnRepository.existsById(id)) {
            throw new ResourceNotFoundException("Retour non trouvé avec l'ID : " + id);
        }
        returnRepository.deleteById(id);
    }

    private ReturnResponse mapToReturnResponse(Return ret) {
        return ReturnResponse.builder()
                .id(ret.getId())
                .loanId(ret.getLoanId())
                .returnDate(ret.getReturnDate())
                .effectiveReturnDate(ret.getEffectiveReturnDate())
                .status(ret.getStatus())
                .build();
    }
}