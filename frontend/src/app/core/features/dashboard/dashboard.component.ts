import { Component, OnInit, signal, WritableSignal, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list'; // Pour afficher des listes d'éléments
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner'; // Pour l'état de chargement
import { MatDividerModule } from '@angular/material/divider';
import { Router } from '@angular/router';
import { LoanResponse, ReservationResponse } from '../../models/loans.interface';
import { Role } from '../../models/role.enum';
import { AuthService } from '../../services/auth.service';
import { BookService } from '../../services/book.service';
import { LoanService } from '../../services/loan.service';
import { UserService } from '../../services/user.service';
// Importez l'enum Role

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatListModule,
    MatProgressSpinnerModule,
    MatDividerModule
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  currentUser = this.authService.currentUser; // Signal de l'utilisateur courant

  // Signals pour stocker les données spécifiques au rôle
  loadingData = signal(false);

  // Données pour ADHÉRENT
  myLoans: WritableSignal<LoanResponse[]> = signal([]);
  myReservations: WritableSignal<ReservationResponse[]> = signal([]);

  // Données pour BIBLIOTHÉCAIRE
  totalBooks = signal(0);
  totalAuthors = signal(0);
  activeLoansCount = signal(0);
  pendingReservationsCount = signal(0);

  // Données pour ADMIN (agrégées ou spécifiques)
  totalUsers = signal(0);
  usersByRole: WritableSignal<{ role: Role, count: number }[]> = signal([]);

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private bookService: BookService,
    private loanService: LoanService,
    private router: Router
  ) {
    // Utiliser un `effect` pour réagir aux changements de `currentUser`
    // et charger les données du dashboard dynamiquement.
    effect(() => {
      const user = this.currentUser();
      if (user) {
        this.loadDashboardData(user.role, user.id);
      } else {
        // Nettoyer les données si l'utilisateur se déconnecte
        this.clearDashboardData();
      }
    }, { allowSignalWrites: true }); // Autorise les écritures de signal à l'intérieur de l'effet
  }

  ngOnInit(): void {
    // ngOnInit sera appelé une fois au chargement du composant.
    // L'effet ci-dessus gère le chargement initial et les changements de connexion.
  }

  private clearDashboardData(): void {
    this.myLoans.set([]);
    this.myReservations.set([]);
    this.totalBooks.set(0);
    this.totalAuthors.set(0);
    this.activeLoansCount.set(0);
    this.pendingReservationsCount.set(0);
    this.totalUsers.set(0);
    this.usersByRole.set([]);
  }

  private loadDashboardData(role: Role, userId: string): void {
    this.loadingData.set(true);
    switch (role) {
      case Role.ADHÉRENT:
        this.fetchAdherentData(userId);
        break;
      case Role.BIBLIOTHÉCAIRE:
        this.fetchBibliothecaireData();
        break;
      case Role.ADMIN:
        this.fetchAdminData();
        break;
      default:
        this.loadingData.set(false);
        break;
    }
  }

  private fetchAdherentData(userId: string): void {
    // Récupérer les emprunts de l'utilisateur
    this.loanService.getLoansByUserId(userId).subscribe({
      next: (loans) => {
        this.myLoans.set(loans.filter(loan => loan.status === 'EN_COURS'));
        this.loadingData.set(false);
      },
      error: (err) => {
        console.error('Error fetching user loans:', err);
        this.loadingData.set(false);
      }
    });

    // Récupérer les réservations de l'utilisateur
    this.loanService.getReservationsByUserId(userId).subscribe({
      next: (reservations) => {
        this.myReservations.set(reservations.filter(res => res.status === 'EN_ATTENTE' || res.status === 'PRÊT_À_RETIRER'));
        this.loadingData.set(false);
      },
      error: (err) => {
        console.error('Error fetching user reservations:', err);
        this.loadingData.set(false);
      }
    });
  }

  private fetchBibliothecaireData(): void {
    // Compter le total des livres
    this.bookService.getAllBooks().subscribe({
      next: (books) => {
        this.totalBooks.set(books.length);
        this.activeLoansCount.set(books.filter(b => b.nombreExemplaires === 0).length); // Livres non disponibles
        this.loadingData.set(false);
      },
      error: (err) => {
        console.error('Error fetching all books:', err);
        this.loadingData.set(false);
      }
    });

    // Compter le total des auteurs
    this.bookService.getAllBooks().subscribe({ // On peut récupérer les auteurs des livres ou faire un appel à ms-books/authors
      next: (books) => {
        const uniqueAuthors = new Set<string>();
        books.forEach(book => book.authors.forEach(author => uniqueAuthors.add(author.id)));
        this.totalAuthors.set(uniqueAuthors.size);
        this.loadingData.set(false);
      },
      error: (err) => {
        console.error('Error fetching authors:', err);
        this.loadingData.set(false);
      }
    });

    // Compter les emprunts actifs
    this.loanService.getAllLoans().subscribe({
      next: (loans) => {
        this.activeLoansCount.set(loans.filter(loan => loan.status === 'EN_COURS').length);
        this.loadingData.set(false);
      },
      error: (err) => {
        console.error('Error fetching all loans:', err);
        this.loadingData.set(false);
      }
    });

    // Compter les réservations en attente
    this.loanService.getAllReservations().subscribe({
      next: (reservations) => {
        this.pendingReservationsCount.set(reservations.filter(res => res.status === 'EN_ATTENTE' || res.status === 'PRÊT_À_RETIRER').length);
        this.loadingData.set(false);
      },
      error: (err) => {
        console.error('Error fetching all reservations:', err);
        this.loadingData.set(false);
      }
    });
  }

  private fetchAdminData(): void {
    this.fetchBibliothecaireData(); // Les admins ont aussi accès aux données de bibliothécaires

    // Compter le total des utilisateurs et leur répartition par rôle
    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.totalUsers.set(users.length);
        const rolesCount = users.reduce((acc, user) => {
          acc[user.role] = (acc[user.role] || 0) + 1;
          return acc;
        }, {} as Record<Role, number>);

        const usersByRoleArray = Object.keys(rolesCount).map(roleKey => ({
          role: roleKey as Role,
          count: rolesCount[roleKey as Role]
        }));
        this.usersByRole.set(usersByRoleArray);
        this.loadingData.set(false);
      },
      error: (err) => {
        console.error('Error fetching all users:', err);
        this.loadingData.set(false);
      }
    });
  }

  logout(): void {
    this.authService.logout();
  }

  // Fonctions de navigation (déjà dans app.component, mais peut être ici aussi pour les boutons spécifiques)
  goToAdminUsers(): void {
    this.router.navigate(['/admin/users']);
  }

  goToAdminBooks(): void {
    this.router.navigate(['/admin/books']);
  }

  goToLoans(): void {
    this.router.navigate(['/loans']);
  }
}
