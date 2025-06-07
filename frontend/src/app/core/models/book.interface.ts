// src/app/core/models/book.interface.ts

export interface AuthorResponse {
  id: string;
  nom: string;
  prenom: string;
  dateNaissance?: string;
  profil?: string;
  bibliographie?: string;
}

export interface BookResponse {
  id: string;
  code: string;
  titre: string;
  edition?: string;
  profil?: string; // Description du livre
  isbn?: string;
  nombreExemplaires: number;
  authors: AuthorResponse[]; // Liste des auteurs associés
}

// DTOs de requête pour la création/mise à jour
export interface AuthorRequest {
  nom: string;
  prenom: string;
  dateNaissance?: string;
  profil?: string;
  bibliographie?: string;
}

export interface BookRequest {
  code: string;
  titre: string;
  edition?: string;
  profil?: string; // Description du livre
  isbn?: string;
  nombreExemplaires: number;
  authorIds?: string[]; // IDs des auteurs (UUIDs en string)
}

export interface BookStockUpdateRequest {
    change: number;
}
