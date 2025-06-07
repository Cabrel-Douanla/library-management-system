import {
  Component,
  OnInit,
  signal,
  WritableSignal,
  ViewChild,
  AfterViewInit,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTabsModule } from '@angular/material/tabs'; // For tabs
import { BookResponse } from '../../../models/book.interface';
import { DialogData, DialogResult } from '../../../models/dialog.interface';
import {
  LoanResponse,
  ReturnResponse,
  ReservationResponse,
  LoanStatus,
  ReservationStatus,
  ReturnStatus,
  LoanRequest,
  ReturnRequest,
  ReservationRequest,
} from '../../../models/loans.interface';
import { User } from '../../../models/user.interface';
import { AuthService } from '../../../services/auth.service';
import { BookService } from '../../../services/book.service';
import { LoanService } from '../../../services/loan.service';
import { UserService } from '../../../services/user.service';
import { ConfirmationDialogComponent } from '../../books/shared/components/confirmation-dialog/confirmation-dialog-component';
import { LoanFormDialogComponent } from '../components/loan-form-dialog/loan-form.dialog.component';
import { ReservationFormDialogComponent } from '../components/reservation-form-dialog/reservation-form-dialog.component';
import { ReturnFormDialogComponent } from '../components/return-form-dialog/return-form-dialog.component';
import { ReplaceUnderscoresPipe } from '../../../../shared/pipes/replace-underscores.pipe';

@Component({
  selector: 'app-loan-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule,
    MatCardModule,
    MatDialogModule,
    MatTabsModule,
    ReplaceUnderscoresPipe,
  ],
  templateUrl: './loan-list.component.html',
  styleUrls: ['./loan-list.component.scss'],
})
export class LoanListComponent implements OnInit, AfterViewInit {
  // Common for all tables
  loading = signal(false);
  usersMap: WritableSignal<Map<string, User>> = signal(new Map());
  booksMap: WritableSignal<Map<string, BookResponse>> = signal(new Map());

  // Loans Tab
  displayedLoansColumns: string[] = [
    'userId',
    'bookId',
    'loanDate',
    'status',
    'actions',
  ];
  loansDataSource = new MatTableDataSource<LoanResponse>();
  @ViewChild('loansPaginator') loansPaginator!: MatPaginator;
  @ViewChild('loansSort') loansSort!: MatSort;

  // Returns Tab
  displayedReturnsColumns: string[] = [
    'loanId',
    'effectiveReturnDate',
    'status',
    'actions',
  ];
  returnsDataSource = new MatTableDataSource<ReturnResponse>();
  @ViewChild('returnsPaginator') returnsPaginator!: MatPaginator;
  @ViewChild('returnsSort') returnsSort!: MatSort;

  // Reservations Tab
  displayedReservationsColumns: string[] = [
    'userId',
    'bookId',
    'reservationDate',
    'status',
    'actions',
  ];
  reservationsDataSource = new MatTableDataSource<ReservationResponse>();
  @ViewChild('reservationsPaginator') reservationsPaginator!: MatPaginator;
  @ViewChild('reservationsSort') reservationsSort!: MatSort;

  // Enums for dropdowns
  loanStatuses = Object.values(LoanStatus);
  reservationStatuses = Object.values(ReservationStatus);
  returnStatuses = Object.values(ReturnStatus);

  // User roles for conditional display
  isAdmin = this.authService.isAdmin;
  isBibliothecaire = this.authService.isBibliothecaire;
  isAdherent = this.authService.isAdherent;
  currentUserId = this.authService.currentUser()?.id;

  constructor(
    private loanService: LoanService,
    private userService: UserService,
    private bookService: BookService,
    private authService: AuthService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadAllReferenceData();
    this.loadAllManagementData();
  }

  ngAfterViewInit(): void {
    this.loansDataSource.paginator = this.loansPaginator;
    this.loansDataSource.sort = this.loansSort;
    this.returnsDataSource.paginator = this.returnsPaginator;
    this.returnsDataSource.sort = this.returnsSort;
    this.reservationsDataSource.paginator = this.reservationsPaginator;
    this.reservationsDataSource.sort = this.reservationsSort;

    // Custom filter predicates (optional, but good for searching by user/book names)
    this.loansDataSource.filterPredicate = (data, filter) =>
      this.filterData(data, filter);
    this.returnsDataSource.filterPredicate = (data, filter) =>
      this.filterData(data, filter);
    this.reservationsDataSource.filterPredicate = (data, filter) =>
      this.filterData(data, filter);
  }

  // --- Data Loading ---
  loadAllReferenceData(): void {
    this.loading.set(true);
    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.usersMap.set(new Map(users.map((u) => [u.id, u])));
        // console.log('Users loaded:', this.usersMap().size);
      },
      error: (err) => console.error('Failed to load users for lookup:', err),
    });

    this.bookService.getAllBooks().subscribe({
      next: (books) => {
        this.booksMap.set(new Map(books.map((b) => [b.id, b])));
        // console.log('Books loaded:', this.booksMap().size);
        this.loading.set(false);
      },
      error: (err) => console.error('Failed to load books for lookup:', err),
    });
  }

  loadAllManagementData(): void {
    this.loading.set(true);
    const userId = this.authService.currentUser()?.id;

    // Load Loans
    if (this.isAdmin() || this.isBibliothecaire()) {
      this.loanService.getAllLoans().subscribe({
        next: (data) => (this.loansDataSource.data = data),
        error: (err) => console.error('Failed to load all loans:', err),
      });
    } else if (this.isAdherent() && userId) {
      this.loanService.getLoansByUserId(userId).subscribe({
        next: (data) => (this.loansDataSource.data = data),
        error: (err) => console.error('Failed to load user loans:', err),
      });
    }

    // Load Returns (usually only bibliothecaire/admin)
    if (this.isAdmin() || this.isBibliothecaire()) {
      this.loanService.getAllReturns().subscribe({
        next: (data) => (this.returnsDataSource.data = data),
        error: (err) => console.error('Failed to load all returns:', err),
      });
    }

    // Load Reservations
    if (this.isAdmin() || this.isBibliothecaire()) {
      this.loanService.getAllReservations().subscribe({
        next: (data) => (this.reservationsDataSource.data = data),
        error: (err) => console.error('Failed to load all reservations:', err),
      });
    } else if (this.isAdherent() && userId) {
      this.loanService.getReservationsByUserId(userId).subscribe({
        next: (data) => (this.reservationsDataSource.data = data),
        error: (err) => console.error('Failed to load user reservations:', err),
      });
    }

    this.loading.set(false);
  }

  // --- Filter and Search ---
  applyFilter(event: Event, dataSource: MatTableDataSource<any>): void {
    const filterValue = (event.target as HTMLInputElement).value;
    dataSource.filter = filterValue.trim().toLowerCase();

    if (dataSource.paginator) {
      dataSource.paginator.firstPage();
    }
  }

  filterData(data: any, filter: string): boolean {
    const user = this.usersMap().get(data.userId || data.id); // Assuming data.id for Reservation/Loan directly
    const book = this.booksMap().get(data.bookId || data.id); // Assuming data.id for Reservation/Loan directly

    const userDisplayName = user
      ? `${user.prenom} ${user.nom} (${user.email})`
      : '';
    const bookTitle = book ? book.titre : '';
    const status = data.status || '';

    // Adjust fields based on the data type (Loan, Return, Reservation)
    let dataStr = '';
    if ('loanDate' in data) {
      // It's a Loan
      dataStr = `${data.loanDate} ${data.status} ${userDisplayName} ${bookTitle}`;
    } else if ('effectiveReturnDate' in data) {
      // It's a Return
      const loan = this.loansDataSource.data.find((l) => l.id === data.loanId);
      const originalBookTitle = loan
        ? this.booksMap().get(loan.bookId)?.titre || ''
        : '';
      dataStr = `${data.effectiveReturnDate} ${data.status} ${originalBookTitle}`;
    } else if ('reservationDate' in data) {
      // It's a Reservation
      dataStr = `${data.reservationDate} ${data.status} ${userDisplayName} ${bookTitle}`;
    }

    return dataStr.toLowerCase().includes(filter);
  }

  // --- Helper Methods for Display ---
  getUserDisplayName(userId: string): string {
    const user = this.usersMap().get(userId);
    return user
      ? `${user.prenom} ${user.nom} (${user.email})`
      : 'Utilisateur inconnu';
  }

  getBookTitle(bookId: string): string {
    const book = this.booksMap().get(bookId);
    return book ? book.titre : 'Livre inconnu';
  }

  // --- Loan Actions ---
  openLoanFormDialog(loan?: LoanResponse): void {
    const dialogData: DialogData<LoanResponse> = {
      title: loan ? "Modifier l'emprunt" : 'Enregistrer un nouvel emprunt',
      mode: loan ? 'edit' : 'create',
      data: loan,
    };

    const dialogRef = this.dialog.open(LoanFormDialogComponent, {
      width: '600px',
      data: dialogData,
    });

    dialogRef.afterClosed().subscribe((result: DialogResult<LoanRequest>) => {
      if (result && result.action === 'save' && result.data) {
        this.loading.set(true);
        if (dialogData.mode === 'create') {
          this.loanService.createLoan(result.data).subscribe({
            next: () => {
              this.snackBar.open('Emprunt enregistré avec succès!', 'Fermer', {
                duration: 3000,
              });
              this.loadAllManagementData();
            },
            error: (err) => console.error('Failed to create loan:', err),
          });
        } else {
          // Update loan status (only editable field in dialog)
          this.snackBar.open(
            "L'édition d'emprunts n'est pas supportée pour le moment.",
            'Fermer',
            { duration: 3000 }
          );
          this.loading.set(false);
          // Example of updating status only:
          // this.loanService.updateLoanStatus(loan!.id, result.data.status).subscribe(...)
        }
      }
    });
  }

  updateLoanStatus(id: string, currentStatus: LoanStatus): void {
    // Logic to determine next status or open dialog for selection
    // For simplicity, let's assume direct status change
    const nextStatus =
      currentStatus === LoanStatus.EN_COURS
        ? LoanStatus.EN_RETARD
        : currentStatus; // Example transition
    this.dialog
      .open(ConfirmationDialogComponent, {
        data: {
          message: `Voulez-vous changer le statut de l'emprunt à "${nextStatus.replace(
            /_/g,
            ' '
          )}"?`,
        },
      })
      .afterClosed()
      .subscribe((confirmed) => {
        if (confirmed) {
          this.loading.set(true);
          this.loanService.updateLoanStatus(id, nextStatus).subscribe({
            next: () => {
              this.snackBar.open("Statut d'emprunt mis à jour!", 'Fermer', {
                duration: 3000,
              });
              this.loadAllManagementData();
            },
            error: (err) => console.error('Failed to update loan status:', err),
          });
        }
      });
  }

  deleteLoan(id: string): void {
    this.dialog
      .open(ConfirmationDialogComponent, {
        data: {
          message:
            'Êtes-vous sûr de vouloir supprimer cet emprunt ? Cette action est irréversible.',
        },
      })
      .afterClosed()
      .subscribe((confirmed) => {
        if (confirmed) {
          this.loading.set(true);
          this.loanService.deleteLoan(id).subscribe({
            next: () => {
              this.snackBar.open('Emprunt supprimé!', 'Fermer', {
                duration: 3000,
              });
              this.loadAllManagementData();
            },
            error: (err) => console.error('Failed to delete loan:', err),
          });
        }
      });
  }

  // --- Return Actions ---
  openRecordReturnDialog(loan?: LoanResponse): void {
    const dialogData: DialogData<
      ReturnResponse & { loanDetails: LoanResponse | null }
    > = {
      title: 'Enregistrer un retour',
      mode: 'create',
      data: { loanDetails: loan || null } as any, // Pass loan data if provided
    };

    const dialogRef = this.dialog.open(ReturnFormDialogComponent, {
      width: '600px',
      data: dialogData,
    });

    dialogRef.afterClosed().subscribe((result: DialogResult<ReturnRequest>) => {
      if (result && result.action === 'save' && result.data) {
        this.loading.set(true);
        this.loanService.recordReturn(result.data).subscribe({
          next: () => {
            this.snackBar.open('Retour enregistré avec succès!', 'Fermer', {
              duration: 3000,
            });
            this.loadAllManagementData();
          },
          error: (err) => console.error('Failed to record return:', err),
        });
      }
    });
  }

  updateReturnStatus(id: string, currentStatus: ReturnStatus): void {
    // Example: Cycle through statuses or open a select dialog
    const nextStatus =
      currentStatus === ReturnStatus.TRAITÉ
        ? ReturnStatus.ENDOMMAGÉ
        : ReturnStatus.TRAITÉ; // Simplified
    this.dialog
      .open(ConfirmationDialogComponent, {
        data: {
          message: `Voulez-vous changer le statut de retour à "${nextStatus.replace(
            /_/g,
            ' '
          )}"?`,
        },
      })
      .afterClosed()
      .subscribe((confirmed) => {
        if (confirmed) {
          this.loading.set(true);
          this.loanService.updateReturnStatus(id, nextStatus).subscribe({
            next: () => {
              this.snackBar.open('Statut de retour mis à jour!', 'Fermer', {
                duration: 3000,
              });
              this.loadAllManagementData();
            },
            error: (err) =>
              console.error('Failed to update return status:', err),
          });
        }
      });
  }

  deleteReturn(id: string): void {
    this.dialog
      .open(ConfirmationDialogComponent, {
        data: {
          message:
            'Êtes-vous sûr de vouloir supprimer cet enregistrement de retour ?',
        },
      })
      .afterClosed()
      .subscribe((confirmed) => {
        if (confirmed) {
          this.loading.set(true);
          this.loanService.deleteReturn(id).subscribe({
            next: () => {
              this.snackBar.open(
                'Enregistrement de retour supprimé!',
                'Fermer',
                { duration: 3000 }
              );
              this.loadAllManagementData();
            },
            error: (err) => console.error('Failed to delete return:', err),
          });
        }
      });
  }

  // --- Reservation Actions ---
  openReservationFormDialog(reservation?: ReservationResponse): void {
    const dialogData: DialogData<ReservationResponse> = {
      title: reservation
        ? 'Modifier la réservation'
        : 'Créer une nouvelle réservation',
      mode: reservation ? 'edit' : 'create',
      data: reservation,
    };

    const dialogRef = this.dialog.open(ReservationFormDialogComponent, {
      width: '600px',
      data: dialogData,
    });

    dialogRef
      .afterClosed()
      .subscribe((result: DialogResult<ReservationRequest>) => {
        if (result && result.action === 'save' && result.data) {
          this.loading.set(true);
          if (dialogData.mode === 'create') {
            this.loanService.createReservation(result.data).subscribe({
              next: () => {
                this.snackBar.open('Réservation créée avec succès!', 'Fermer', {
                  duration: 3000,
                });
                this.loadAllManagementData();
              },
              error: (err) =>
                console.error('Failed to create reservation:', err),
            });
          } else {
            // Update reservation status / validity (only editable in dialog)
            this.loanService
              .updateReservationStatus(
                reservation!.id,
                result.data.status as ReservationStatus
              )
              .subscribe({
                next: () => {
                  this.snackBar.open('Réservation mise à jour!', 'Fermer', {
                    duration: 3000,
                  });
                  this.loadAllManagementData();
                },
                error: (err) =>
                  console.error('Failed to update reservation:', err),
              });
            // Update validityDurationDays if needed as well (requires separate backend endpoint or combined PUT)
          }
        }
      });
  }

  convertReservationToLoan(id: string): void {
    this.dialog
      .open(ConfirmationDialogComponent, {
        data: {
          message:
            'Voulez-vous convertir cette réservation en emprunt ? Cela réduira le stock du livre.',
        },
      })
      .afterClosed()
      .subscribe((confirmed) => {
        if (confirmed) {
          this.loading.set(true);
          this.loanService.convertReservationToLoan(id).subscribe({
            next: () => {
              this.snackBar.open(
                'Réservation convertie en emprunt!',
                'Fermer',
                { duration: 3000 }
              );
              this.loadAllManagementData();
            },
            error: (err) =>
              console.error('Failed to convert reservation to loan:', err),
          });
        }
      });
  }

  deleteReservation(id: string): void {
    this.dialog
      .open(ConfirmationDialogComponent, {
        data: {
          message: 'Êtes-vous sûr de vouloir supprimer cette réservation ?',
        },
      })
      .afterClosed()
      .subscribe((confirmed) => {
        if (confirmed) {
          this.loading.set(true);
          this.loanService.deleteReservation(id).subscribe({
            next: () => {
              this.snackBar.open('Réservation supprimée!', 'Fermer', {
                duration: 3000,
              });
              this.loadAllManagementData();
            },
            error: (err) => console.error('Failed to delete reservation:', err),
          });
        }
      });
  }
}
