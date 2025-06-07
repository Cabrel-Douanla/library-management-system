import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import {
  User,
  UserUpdateRequest,
  UserRegistrationRequest,
} from '../models/user.interface';
import { Role } from '../models/role.enum'; // Importez l'enum Role

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private userApiUrl = `${environment.apiUrl}/users`; // Note: Base URL for users is /api/users in backend

  constructor(private http: HttpClient) {}

  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.userApiUrl);
  }

  getUserById(id: string): Observable<User> {
    return this.http.get<User>(`${this.userApiUrl}/${id}`);
  }

  // Utilisé par le contrôleur Auth pour l'inscription (non exposé directement ici pour le CRUD admin)
  // registerUser(user: UserRegistrationRequest): Observable<User> {
  //   return this.http.post<User>(`${environment.apiUrl}/auth/register`, user);
  // }

  // Pour l'administrateur: création d'un utilisateur directement (sans mot de passe fourni initialement)
  // OU pour l'administrateur, création d'un utilisateur avec un mot de passe temporaire
  createUser(user: UserRegistrationRequest): Observable<User> {
    return this.http.post<User>(`${this.userApiUrl}`, user); // Si backend a un endpoint /users POST
  }

  updateUserDetails(id: string, user: UserUpdateRequest): Observable<User> {
    return this.http.put<User>(`${this.userApiUrl}/${id}`, user);
  }

  updateUserRole(id: string, newRole: Role): Observable<User> {
    const params = new HttpParams().set('newRole', newRole);
    return this.http.put<User>(`${this.userApiUrl}/${id}/role`, null, {
      params,
    });
  }

  deleteUser(id: string): Observable<void> {
    return this.http.delete<void>(`${this.userApiUrl}/${id}`);
  }
}
