// src/app/core/models/loan.interface.ts

// ENUMS (à placer idéalement dans un fichier `loan-status.enum.ts`, etc. pour plus de clarté)
export enum LoanStatus {
    EN_COURS = 'EN_COURS',
    TERMINÉ = 'TERMINÉ',
    ANNULÉ = 'ANNULÉ',
    EN_RETARD = 'EN_RETARD'
}

export enum ReturnStatus {
    EN_ATTENTE = 'EN_ATTENTE',
    TRAITÉ = 'TRAITÉ',
    ENDOMMAGÉ = 'ENDOMMAGÉ',
    PERDU = 'PERDU'
}

export enum ReservationStatus {
    EN_ATTENTE = 'EN_ATTENTE',
    PRÊT_À_RETIRER = 'PRÊT_À_RETIRER',
    ANNULÉE = 'ANNULÉE',
    EXPIRÉE = 'EXPIRÉE',
    CONVERTIE_EN_EMPRUNT = 'CONVERTIE_EN_EMPRUNT'
}

// DTOs de Réponse
export interface LoanResponse {
  loanId: string;
  returnDate: string;
  id: string;
  userId: string;
  bookId: string;
  loanDate: string;
  status: LoanStatus;
}

export interface ReturnResponse {
  id: string;
  loanId: string;
  returnDate: string;
  effectiveReturnDate?: string;
  status: ReturnStatus;
}

export interface ReservationResponse {
  id: string;
  userId: string;
  bookId: string;
  reservationDate: string;
  validityDurationDays: number;
  effectiveLoanDate?: string;
  status: ReservationStatus;
}

// DTOs de Requête
export interface LoanRequest {
    userId: string;
    bookId: string;
    loanDate?: string; // Format YYYY-MM-DD
}

export interface ReturnRequest {
    loanId: string;
    effectiveReturnDate?: string;
}

export interface ReservationRequest {
    status?: ReservationStatus;
    userId: string;
    bookId: string;
    reservationDate?: string; // Format YYYY-MM-DD
    validityDurationDays?: number;
}
