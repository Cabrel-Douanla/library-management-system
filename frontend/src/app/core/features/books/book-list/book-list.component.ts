// src/app/features/books/book-list/book-list.component.ts

import { Component, OnInit, signal, WritableSignal, ViewChild, AfterViewInit } from '@angular/core'; // Ajoutez AfterViewInit
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
import { BookFormDialogComponent } from '../components/book-form-dialog/book-form-dialog.component';
import { BookResponse, AuthorResponse, BookRequest, AuthorRequest } from '../../../models/book.interface';
import { DialogData, DialogResult } from '../../../models/dialog.interface';
import { AuthService } from '../../../services/auth.service';
import { BookService } from '../../../services/book.service';
import { AuthorFormDialogComponent } from '../components/author-form-dialog/author-form-dialog.component';
import { ConfirmationDialogComponent } from '../shared/components/confirmation-dialog/confirmation-dialog-component';

@Component({
  selector: 'app-book-list',
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
  templateUrl: './book-list.component.html',
  styleUrls: ['./book-list.component.scss']
})
export class BookListComponent implements OnInit, AfterViewInit { // Implémentez AfterViewInit
  displayedColumns: string[] = ['code', 'titre', 'authors', 'isbn', 'nombreExemplaires', 'actions'];
  dataSource = new MatTableDataSource<BookResponse>();
  books: WritableSignal<BookResponse[]> = signal([]);
  authors: WritableSignal<AuthorResponse[]> = signal([]);
  loadingBooks = signal(false);
  loadingAuthors = signal(false);


  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private bookService: BookService,
    private authService: AuthService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.loadBooks();
    this.loadAuthors();
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.dataSource.filterPredicate = (data: BookResponse, filter: string) => {
      const dataStr = data.titre + data.code + data.isbn + data.authors.map(a => `${a.prenom} ${a.nom}`).join(' ');
      return dataStr.toLowerCase().includes(filter);
    };
  }

  loadBooks(): void {
    this.loadingBooks.set(true);
    this.bookService.getAllBooks().subscribe({
      next: (data) => {
        this.books.set(data);
        this.dataSource.data = data;
        this.loadingBooks.set(false);
      },
      error: (err) => {
        console.error('Failed to load books:', err);
        this.loadingBooks.set(false);
      }
    });
  }

  loadAuthors(): void {
    this.loadingAuthors.set(true);
    this.bookService.getAllAuthors().subscribe({
      next: (data) => {
        this.authors.set(data);
        this.loadingAuthors.set(false);
      },
      error: (err) => {
        console.error('Failed to load authors:', err);
        this.loadingAuthors.set(false);
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

  openBookFormDialog(book?: BookResponse): void {
    const dialogData: DialogData<BookResponse> = {
      title: book ? 'Modifier le livre' : 'Ajouter un nouveau livre',
      mode: book ? 'edit' : 'create', // Le mode est ici dans le data du dialog
      data: book
    };

    const dialogRef = this.dialog.open(BookFormDialogComponent, {
      width: '600px',
      data: dialogData,
    });

    dialogRef.afterClosed().subscribe((result: DialogResult<BookRequest>) => {
      if (result && result.action === 'save' && result.data) {
        this.loadingBooks.set(true);
        // Utiliser le mode du `dialogData`
        if (dialogData.mode === 'create') { // Correction ici : utilisez dialogData.mode
          this.bookService.createBook(result.data).subscribe({
            next: () => {
              this.snackBar.open('Livre ajouté avec succès!', 'Fermer', { duration: 3000 });
              this.loadBooks();
            },
            error: (err) => {
              console.error('Failed to create book:', err);
              this.loadingBooks.set(false);
            }
          });
        } else { // Edit mode
          this.bookService.updateBook(book!.id, result.data).subscribe({
            next: () => {
              this.snackBar.open('Livre mis à jour avec succès!', 'Fermer', { duration: 3000 });
              this.loadBooks();
            },
            error: (err) => {
              console.error('Failed to update book:', err);
              this.loadingBooks.set(false);
            }
          });
        }
      }
    });
  }

  deleteBook(id: string): void {
    this.dialog.open(ConfirmationDialogComponent, {
      data: { message: 'Êtes-vous sûr de vouloir supprimer ce livre ?' }
    }).afterClosed().subscribe(confirmed => {
      if (confirmed) {
        this.loadingBooks.set(true);
        this.bookService.deleteBook(id).subscribe({
          next: () => {
            this.snackBar.open('Livre supprimé avec succès!', 'Fermer', { duration: 3000 });
            this.loadBooks();
          },
          error: (err) => {
            console.error('Failed to delete book:', err);
            this.loadingBooks.set(false);
          }
        });
      }
    });
  }

  openAuthorFormDialog(author?: AuthorResponse): void {
    const dialogData: DialogData<AuthorResponse> = {
      title: author ? 'Modifier l\'auteur' : 'Ajouter un nouvel auteur',
      mode: author ? 'edit' : 'create',
      data: author
    };

    const dialogRef = this.dialog.open(AuthorFormDialogComponent, {
      width: '600px',
      data: dialogData,
    });

    dialogRef.afterClosed().subscribe((result: DialogResult<AuthorRequest>) => {
      if (result && result.action === 'save' && result.data) {
        this.loadingAuthors.set(true);
        // Utiliser le mode du `dialogData`
        if (dialogData.mode === 'create') { // Correction ici : utilisez dialogData.mode
          this.bookService.createAuthor(result.data).subscribe({
            next: () => {
              this.snackBar.open('Auteur ajouté avec succès!', 'Fermer', { duration: 3000 });
              this.loadAuthors();
            },
            error: (err) => {
              console.error('Failed to create author:', err);
              this.loadingAuthors.set(false);
            }
          });
        } else { // Edit mode
          this.bookService.updateAuthor(author!.id, result.data).subscribe({
            next: () => {
              this.snackBar.open('Auteur mis à jour avec succès!', 'Fermer', { duration: 3000 });
              this.loadAuthors();
            },
            error: (err) => {
              console.error('Failed to update author:', err);
              this.loadingAuthors.set(false);
            }
          });
        }
      }
    });
  }

  deleteAuthor(id: string): void {
    this.dialog.open(ConfirmationDialogComponent, {
      data: { message: 'Êtes-vous sûr de vouloir supprimer cet auteur ?' }
    }).afterClosed().subscribe(confirmed => {
      if (confirmed) {
        this.loadingAuthors.set(true);
        this.bookService.deleteAuthor(id).subscribe({
          next: () => {
            this.snackBar.open('Auteur supprimé avec succès!', 'Fermer', { duration: 3000 });
            this.loadAuthors();
            this.loadBooks(); // Recharger les livres au cas où un auteur supprimé leur est associé
          },
          error: (err) => {
            console.error('Failed to delete author:', err);
            this.loadingAuthors.set(false);
          }
        });
      }
    });
  }

  // NOUVELLE MÉTHODE POUR FORMATTER LES AUTEURS
  getAuthorsDisplay(authors: AuthorResponse[] | undefined): string {
    if (!authors || authors.length === 0) {
      return 'N/A';
    }
    return authors.map(a => `${a.prenom} ${a.nom}`).join(', ');
  }
}
