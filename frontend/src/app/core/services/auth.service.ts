import { Injectable, signal, WritableSignal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { LoginResponse, User } from '../models/user.interface';
import { Role } from '../models/role.enum';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private authApiUrl = `${environment.apiUrl}/auth`;

  // Signals pour l'état de l'authentification
  currentUser: WritableSignal<User | null> = signal(null);
  isLoggedIn = computed(() => !!this.currentUser());
  isAdmin = computed(() => this.currentUser()?.role === Role.ADMIN);
  isBibliothecaire = computed(() => this.currentUser()?.role === Role.BIBLIOTHÉCAIRE);
  isAdherent = computed(() => this.currentUser()?.role === Role.ADHÉRENT);

  constructor(private http: HttpClient, private router: Router) {
    this.loadUserFromLocalStorage();
  }

  private loadUserFromLocalStorage(): void {
    const token = localStorage.getItem('jwt_token');
    const userJson = localStorage.getItem('current_user');
    if (token && userJson) {
      try {
        const user: User = JSON.parse(userJson);
        this.currentUser.set(user);
      } catch (e) {
        console.error('Failed to parse user data from localStorage', e);
        this.logout(); // Clear invalid data
      }
    }
  }

  login(credentials: { email: string; password: string }): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.authApiUrl}/login`, credentials).pipe(
      tap(response => {
        localStorage.setItem('jwt_token', response.jwtToken);
        const user: User = { // Construire un objet User minimal à partir de la réponse du login
          email: response.userEmail,
          role: Role[response.userRole as keyof typeof Role], // Convertir string en enum
          // Ajoutez d'autres propriétés si votre LoginResponse les contient
          id: '', // Placeholder, sera mis à jour si besoin
          code: '', nom: '', prenom: '', etat: ''
        };
        localStorage.setItem('current_user', JSON.stringify(user));
        this.currentUser.set(user);
      })
    );
  }

  register(userData: any): Observable<User> { // Utilisez un DTO spécifique pour l'enregistrement si besoin
    return this.http.post<User>(`${this.authApiUrl}/register`, userData);
  }

  logout(): void {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('current_user');
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem('jwt_token');
  }

  // Méthode pour vérifier si un utilisateur a un rôle spécifique (utile pour les gardes et l'affichage conditionnel)
  hasRole(requiredRoles: Role[]): boolean {
    if (!this.currentUser()) {
      return false;
    }
    return requiredRoles.includes(this.currentUser()!.role);
  }
}
