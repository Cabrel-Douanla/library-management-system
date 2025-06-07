import { Role } from './role.enum';

// DTO de Réponse (UserResponse du backend)
export interface User {
  id: string;
  code: string;
  nom: string;
  prenom: string;
  dateNaissance?: string; // LocalDate en backend
  sexe?: string;
  adresse?: string;
  email: string;
  role: Role;
  etat: string; // "actif", "inactif", "suspendu"
}

export interface LoginResponse {
  jwtToken: string;
  userRole: string; // Ex: "ADMIN", "ADHÉRENT"
  userEmail: string;
}

// DTOs de Requête pour la mise à jour (UserUpdateRequest du backend)
export interface UserUpdateRequest {
  nom?: string;
  prenom?: string;
  dateNaissance?: string;
  sexe?: string;
  adresse?: string;
  email?: string;
  role?: Role;
  etat?: string; // "actif", "inactif", "suspendu"
}

// DTO de Requête pour l'enregistrement (UserRegistrationRequest du backend)
export interface UserRegistrationRequest {
  nom: string;
  prenom: string;
  dateNaissance?: string;
  sexe?: string;
  adresse?: string;
  email: string;
  password?: string; // Pas toujours nécessaire pour la mise à jour, mais pour l'enregistrement
  role: Role;
}
