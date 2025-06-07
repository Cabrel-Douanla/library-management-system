// src/app/features/users/components/user-form-dialog/user-form-dialog.component.ts
import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogActions, MatDialogContent } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatRadioModule } from '@angular/material/radio'; // For gender selection
import { MatProgressBarModule } from '@angular/material/progress-bar'; // If loading options
import { DialogResult, DialogData } from '../../../../models/dialog.interface';
import { Role } from '../../../../models/role.enum';
import {
  UserUpdateRequest,
  UserRegistrationRequest,
  User,
} from '../../../../models/user.interface';

@Component({
  selector: 'app-user-form-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatOptionModule,
    MatDatepickerModule,
    MatRadioModule,
    MatProgressBarModule,
    MatDialogActions,
    MatDialogContent,
  ],
  templateUrl: './user-form.dialog.component.html',
  styleUrls: ['./user-form.dialog.component.scss'],
})
export class UserFormDialogComponent implements OnInit {
  userForm: FormGroup;
  title: string;
  mode: 'create' | 'edit';
  roles = Object.values(Role); // For role selection
  userStates = ['actif', 'inactif', 'suspendu']; // For account state selection

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<
      UserFormDialogComponent,
      DialogResult<UserUpdateRequest | UserRegistrationRequest>
    >,
    @Inject(MAT_DIALOG_DATA) public data: DialogData<User>
  ) {
    this.title = data.title;
    this.mode = data.mode;

    this.userForm = this.fb.group({
      nom: ['', Validators.required],
      prenom: ['', Validators.required],
      dateNaissance: [null],
      sexe: [''],
      adresse: [''],
      email: ['', [Validators.required, Validators.email]],
      // Password is only for 'create' mode, not for 'edit' or it should be a separate field
      password: [''],
      role: [Role.ADHÉRENT, Validators.required],
      etat: ['actif', Validators.required],
    });

    if (this.mode === 'edit' && this.data.data) {
      const user = this.data.data;
      this.userForm.patchValue({
        nom: user.nom,
        prenom: user.prenom,
        dateNaissance: user.dateNaissance ? new Date(user.dateNaissance) : null,
        sexe: user.sexe,
        adresse: user.adresse,
        email: user.email,
        role: user.role,
        etat: user.etat,
      });
      // In edit mode, password is not editable here.
      this.userForm.get('password')?.setValidators(null);
      this.userForm.get('password')?.updateValueAndValidity();
      // Code is not editable
      this.userForm.get('code')?.disable(); // Assuming 'code' field exists in form
    } else {
      // In create mode, password is required
      this.userForm
        .get('password')
        ?.setValidators([Validators.required, Validators.minLength(6)]);
      this.userForm.get('password')?.updateValueAndValidity();
    }
  }

  ngOnInit(): void {}

  onSave(): void {
    if (this.userForm.valid) {
      const formValue = { ...this.userForm.getRawValue() }; // getRawValue to include disabled fields
      if (formValue.dateNaissance) {
        formValue.dateNaissance = formValue.dateNaissance
          .toISOString()
          .split('T')[0];
      }
      this.dialogRef.close({ action: 'save', data: formValue });
    }
  }

  onCancel(): void {
    this.dialogRef.close({ action: 'cancel' });
  }
}
