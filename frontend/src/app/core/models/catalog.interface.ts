export interface CatalogueEntry {
  id: string;
  bookId: string;
  titre: string;
  auteurNom: string;
  description: string;
  genre?: string;
  noteMoyenne?: number;
  datePublication?: string; // ou Date, selon comment vous le gérez
  isbn?: string;
  nombreExemplaires: number;
}

export interface NewArrival {
  id: string;
  bookId: string;
  titre: string;
  auteurNom: string;
  dateAjout: string;
}

export interface Suggestion {
  id: string;
  bookId: string;
  titre: string;
  auteurNom: string;
  categorie: string;
  priorite: number;
}
