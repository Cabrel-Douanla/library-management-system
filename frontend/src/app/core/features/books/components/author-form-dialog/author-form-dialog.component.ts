import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogActions, MatDialogContent } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { AuthorRequest, AuthorResponse } from '../../../../models/book.interface';
import { DialogData, DialogResult } from '../../../../models/dialog.interface';

@Component({
  selector: 'app-author-form-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatDialogActions,
    MatDialogContent
  ],
  templateUrl: './author-form-dialog.component.html',
  styleUrls: ['./author-form-dialog.component.scss']
})
export class AuthorFormDialogComponent implements OnInit {
  authorForm: FormGroup;
  title: string;
  mode: 'create' | 'edit';

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<AuthorFormDialogComponent, DialogResult<AuthorRequest>>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData<AuthorResponse>
  ) {
    this.title = data.title;
    this.mode = data.mode;

    this.authorForm = this.fb.group({
      nom: ['', Validators.required],
      prenom: ['', Validators.required],
      dateNaissance: [null],
      profil: [''],
      bibliographie: ['']
    });

    if (this.mode === 'edit' && this.data.data) {
      const author = this.data.data;
      this.authorForm.patchValue({
        nom: author.nom,
        prenom: author.prenom,
        dateNaissance: author.dateNaissance ? new Date(author.dateNaissance) : null, // Convertir string en Date
        profil: author.profil,
        bibliographie: author.bibliographie
      });
    }
  }

  ngOnInit(): void {}

  onSave(): void {
    if (this.authorForm.valid) {
      // Convertir la date en string au format YYYY-MM-DD pour le backend
      const formValue = { ...this.authorForm.value };
      if (formValue.dateNaissance) {
        formValue.dateNaissance = formValue.dateNaissance.toISOString().split('T')[0];
      }
      this.dialogRef.close({ action: 'save', data: formValue });
    }
  }

  onCancel(): void {
    this.dialogRef.close({ action: 'cancel' });
  }
}
