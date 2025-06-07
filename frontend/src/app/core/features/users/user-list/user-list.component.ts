// src/app/features/users/user-list/user-list.component.ts
import { Component, OnInit, signal, WritableSignal, ViewChild, AfterViewInit } from '@angular/core';
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
import { DialogData, DialogResult } from '../../../models/dialog.interface';
import { User, UserUpdateRequest, UserRegistrationRequest } from '../../../models/user.interface';
import { AuthService } from '../../../services/auth.service';
import { UserService } from '../../../services/user.service';
import { ConfirmationDialogComponent } from '../../books/shared/components/confirmation-dialog/confirmation-dialog-component';
import { UserFormDialogComponent } from '../components/user-form-dialog/user-form-dialog.component';

@Component({
  selector: 'app-user-list',
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
  ],
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss']
})
export class UserListComponent implements OnInit, AfterViewInit {
  displayedColumns: string[] = ['code', 'nom', 'prenom', 'email', 'role', 'etat', 'actions'];
  dataSource = new MatTableDataSource<User>();
  users: WritableSignal<User[]> = signal([]);
  loading = signal(false);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  // Rôles pour les permissions d'affichage des boutons
  isAdmin = this.authService.isAdmin;

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.loadUsers();
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.dataSource.filterPredicate = (data: User, filter: string) => {
      const dataStr = `${data.code} ${data.nom} ${data.prenom} ${data.email} ${data.role} ${data.etat}`;
      return dataStr.toLowerCase().includes(filter);
    };
  }

  loadUsers(): void {
    this.loading.set(true);
    this.userService.getAllUsers().subscribe({
      next: (data) => {
        this.users.set(data);
        this.dataSource.data = data;
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Failed to load users:', err);
        this.loading.set(false);
      }
    });
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  openUserFormDialog(user?: User): void {
    const dialogData: DialogData<User> = {
      title: user ? 'Modifier l\'utilisateur' : 'Créer un nouvel utilisateur',
      mode: user ? 'edit' : 'create',
      data: user
    };

    const dialogRef = this.dialog.open(UserFormDialogComponent, {
      width: '600px',
      data: dialogData,
    });

    dialogRef.afterClosed().subscribe((result: DialogResult<UserUpdateRequest | UserRegistrationRequest>) => {
      if (result && result.action === 'save' && result.data) {
        this.loading.set(true);
        if (dialogData.mode === 'create') {
          // Cast result.data to UserRegistrationRequest for creation
          this.userService.createUser(result.data as UserRegistrationRequest).subscribe({
            next: () => {
              this.snackBar.open('Utilisateur créé avec succès!', 'Fermer', { duration: 3000 });
              this.loadUsers();
            },
            error: (err) => {
              console.error('Failed to create user:', err);
              this.loading.set(false);
            }
          });
        } else {
          // Cast result.data to UserUpdateRequest for update
          this.userService.updateUserDetails(user!.id, result.data as UserUpdateRequest).subscribe({
            next: () => {
              this.snackBar.open('Utilisateur mis à jour avec succès!', 'Fermer', { duration: 3000 });
              this.loadUsers();
            },
            error: (err) => {
              console.error('Failed to update user:', err);
              this.loading.set(false);
            }
          });
        }
      }
    });
  }

  deleteUser(id: string): void {
    this.dialog.open(ConfirmationDialogComponent, {
      data: { message: 'Êtes-vous sûr de vouloir supprimer cet utilisateur ? Cette action est irréversible.' }
    }).afterClosed().subscribe(confirmed => {
      if (confirmed) {
        this.loading.set(true);
        this.userService.deleteUser(id).subscribe({
          next: () => {
            this.snackBar.open('Utilisateur supprimé avec succès!', 'Fermer', { duration: 3000 });
            this.loadUsers();
          },
          error: (err) => {
            console.error('Failed to delete user:', err);
            this.loading.set(false);
          }
        });
      }
    });
  }
}
