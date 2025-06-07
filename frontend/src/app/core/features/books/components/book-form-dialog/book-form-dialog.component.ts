import { Component, Inject, OnInit, signal, WritableSignal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogActions, MatDialogContent } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox'; // Si besoin
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { BookService } from '../../../../services/book.service';
import { AuthorResponse, BookRequest, BookResponse } from '../../../../models/book.interface';
import { DialogResult, DialogData } from '../../../../models/dialog.interface';

@Component({
  selector: 'app-book-form-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatOptionModule,
    MatIconModule,
    MatCheckboxModule,
    MatProgressBarModule,
    MatDialogActions,
    MatDialogContent,
  ],
  templateUrl: './book-form.dialog.component.html',
  styleUrls: ['./book-form-dialog.component.scss']
})
export class BookFormDialogComponent implements OnInit {
  bookForm: FormGroup;
  title: string;
  mode: 'create' | 'edit';
  authors: WritableSignal<AuthorResponse[]> = signal([]);
  loadingAuthors = signal(false);

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<BookFormDialogComponent, DialogResult<BookRequest>>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData<BookResponse>,
    private bookService: BookService
  ) {
    this.title = data.title;
    this.mode = data.mode;

    this.bookForm = this.fb.group({
      code: ['', Validators.required],
      titre: ['', Validators.required],
      edition: [''],
      profil: [''], // Description du livre
      isbn: [''],
      nombreExemplaires: [0, [Validators.required, Validators.min(0)]],
      authorIds: [[]] // Tableau d'UUIDs pour les auteurs
    });

    if (this.mode === 'edit' && this.data.data) {
      const book = this.data.data;
      this.bookForm.patchValue({
        code: book.code,
        titre: book.titre,
        edition: book.edition,
        profil: book.profil,
        isbn: book.isbn,
        nombreExemplaires: book.nombreExemplaires,
        authorIds: book.authors.map(a => a.id) // Pré-sélectionner les auteurs existants
      });
    }
  }

  ngOnInit(): void {
    this.loadAuthors();
  }

  loadAuthors(): void {
    this.loadingAuthors.set(true);
    this.bookService.getAllAuthors().subscribe({
      next: (authors) => {
        this.authors.set(authors);
        this.loadingAuthors.set(false);
      },
      error: (err) => {
        console.error('Failed to load authors:', err);
        this.loadingAuthors.set(false);
        // L'intercepteur d'erreurs s'occupe de la notification
      }
    });
  }

  onSave(): void {
    if (this.bookForm.valid) {
      this.dialogRef.close({ action: 'save', data: this.bookForm.value });
    }
  }

  onCancel(): void {
    this.dialogRef.close({ action: 'cancel' });
  }
}
