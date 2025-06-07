package com.library.management.msloans.service;

import com.library.management.msloans.dto.BookResponse;
import com.library.management.msloans.dto.BookStockUpdateRequest;
import com.library.management.msloans.dto.LoanRequest;
import com.library.management.msloans.dto.LoanResponse;
import com.library.management.msloans.enums.LoanStatus;
import com.library.management.msloans.exception.ResourceNotFoundException;
import com.library.management.msloans.feign.BookClient;
import com.library.management.msloans.feign.UserClient;
import com.library.management.msloans.model.Loan;
import com.library.management.msloans.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserClient userClient; // Feign client pour ms-users
    private final BookClient bookClient; // Feign client pour ms-books

    @Transactional
    public LoanResponse createLoan(LoanRequest request) {
        // 1. Valider l'existence de l'utilisateur via ms-users
        try {
            userClient.getUserById(request.getUserId());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Utilisateur non trouvé avec l'ID : " + request.getUserId());
        }

        // 2. Valider l'existence du livre et sa disponibilité via ms-books
        BookResponse book = null;
        try {
            book = bookClient.getBookById(request.getBookId());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Livre non trouvé avec l'ID : " + request.getBookId());
        }

        if (book.getNombreExemplaires() <= 0) {
            throw new IllegalArgumentException("Le livre '" + book.getTitre() + "' n'est pas disponible pour l'emprunt.");
        }

        // 3. Vérifier si l'utilisateur n'a pas déjà ce livre en cours d'emprunt (optionnel)
        if (loanRepository.findByBookIdAndStatus(request.getBookId(), LoanStatus.EN_COURS).isPresent()) {
            throw new IllegalArgumentException("Ce livre est déjà emprunté par un autre utilisateur.");
        }
        // Si on voulait interdire à un même user d'emprunter le même livre deux fois :
        // if (loanRepository.findByUserIdAndBookIdAndStatus(request.getUserId(), request.getBookId(), LoanStatus.EN_COURS).isPresent()) { ... }


        // 4. Décrémenter le stock dans ms-books
        try {
            bookClient.updateBookStock(request.getBookId(), BookStockUpdateRequest.builder().change(-1).build());
        } catch (Exception e) {
            // Gérer l'échec de la mise à jour du stock (ex: log, re-throw)
            throw new RuntimeException("Erreur lors de la mise à jour du stock du livre : " + e.getMessage());
        }

        // 5. Créer l'emprunt
        Loan loan = Loan.builder()
                .userId(request.getUserId())
                .bookId(request.getBookId())
                .loanDate(request.getLoanDate() != null ? request.getLoanDate() : LocalDate.now())
                .status(LoanStatus.EN_COURS)
                .build();

        Loan savedLoan = loanRepository.save(loan);
        return mapToLoanResponse(savedLoan);
    }

    public List<LoanResponse> getAllLoans() {
        return loanRepository.findAll().stream()
                .map(this::mapToLoanResponse)
                .collect(Collectors.toList());
    }

    public LoanResponse getLoanById(UUID id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Emprunt non trouvé avec l'ID : " + id));
        return mapToLoanResponse(loan);
    }

    public List<LoanResponse> getLoansByUserId(UUID userId) {
        // Validation de l'utilisateur n'est pas nécessaire ici, l'ID peut juste être un filtre.
        return loanRepository.findByUserId(userId).stream()
                .map(this::mapToLoanResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public LoanResponse updateLoanStatus(UUID id, LoanStatus newStatus) {
        Loan existingLoan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Emprunt non trouvé avec l'ID : " + id));

        // Logique de transition de statut (ex: ne peut pas passer de TERMINÉ à EN_COURS)
        if (existingLoan.getStatus() == LoanStatus.TERMINÉ && newStatus == LoanStatus.EN_COURS) {
            throw new IllegalArgumentException("Impossible de réactiver un emprunt terminé.");
        }
        // Autres validations de transition de statut...

        existingLoan.setStatus(newStatus);
        Loan updatedLoan = loanRepository.save(existingLoan);
        return mapToLoanResponse(updatedLoan);
    }

    @Transactional
    public void deleteLoan(UUID id) {
        if (!loanRepository.existsById(id)) {
            throw new ResourceNotFoundException("Emprunt non trouvé avec l'ID : " + id);
        }
        // Logique supplémentaire: Si l'emprunt est en cours, il faut incrémenter le stock du livre
        // ou s'assurer que c'est une suppression d'un emprunt annulé/terminé seulement.
        loanRepository.deleteById(id);
    }

    private LoanResponse mapToLoanResponse(Loan loan) {
        return LoanResponse.builder()
                .id(loan.getId())
                .userId(loan.getUserId())
                .bookId(loan.getBookId())
                .loanDate(loan.getLoanDate())
                .status(loan.getStatus())
                .build();
    }
}