import {
  Component,
  Inject,
  OnInit,
  signal,
  WritableSignal,
} from '@angular/core';
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
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { BookResponse } from '../../../../models/book.interface';
import { DialogResult, DialogData } from '../../../../models/dialog.interface';
import { SelectOption } from '../../../../models/form-options.interface';
import { User } from '../../../../models/user.interface';
import { BookService } from '../../../../services/book.service';
import { UserService } from '../../../../services/user.service';
import {
  ReservationRequest,
  ReservationResponse,
  ReservationStatus,
} from '../../../../models/loans.interface';

@Component({
  selector: 'app-reservation-form-dialog',
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
    MatProgressBarModule,
    MatDialogActions,
    MatDialogContent
  ],
  templateUrl: './reservation-form-dialog.component.html',
  styleUrls: ['./reservation-form-dialog.component.scss'],
})
export class ReservationFormDialogComponent implements OnInit {
  reservationForm: FormGroup;
  title: string;
  mode: 'create' | 'edit';
  loadingData = signal(false);

  userOptions: WritableSignal<SelectOption[]> = signal([]);
  bookOptions: WritableSignal<SelectOption[]> = signal([]);
  reservationStatusOptions: WritableSignal<SelectOption[]> = signal(
    Object.values(ReservationStatus).map((status) => ({
      value: status,
      label: status.replace(/_/g, ' '),
    }))
  );

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<
      ReservationFormDialogComponent,
      DialogResult<ReservationRequest>
    >,
    @Inject(MAT_DIALOG_DATA) public data: DialogData<ReservationResponse>,
    private userService: UserService,
    private bookService: BookService
  ) {
    this.title = data.title;
    this.mode = data.mode;

    this.reservationForm = this.fb.group({
      userId: ['', Validators.required],
      bookId: ['', Validators.required],
      reservationDate: [new Date(), Validators.required],
      validityDurationDays: [7, [Validators.required, Validators.min(1)]],
      status: [ReservationStatus.EN_ATTENTE, Validators.required], // Default status for new
    });

    if (this.mode === 'edit' && this.data.data) {
      const reservation = this.data.data;
      this.reservationForm.patchValue({
        userId: reservation.userId,
        bookId: reservation.bookId,
        reservationDate: new Date(reservation.reservationDate),
        validityDurationDays: reservation.validityDurationDays,
        status: reservation.status,
      });
      // Disable key fields in edit mode
      this.reservationForm.get('userId')?.disable();
      this.reservationForm.get('bookId')?.disable();
      this.reservationForm.get('reservationDate')?.disable();
    }
  }

  ngOnInit(): void {
    this.loadUsersAndBooks();
  }

  loadUsersAndBooks(): void {
    this.loadingData.set(true);
    this.userService.getAllUsers().subscribe({
      next: (users: User[]) => {
        this.userOptions.set(
          users.map((u) => ({
            value: u.id,
            label: `${u.prenom} ${u.nom} (${u.email})`,
          }))
        );
        this.loadingData.set(false);
      },
      error: (err) => console.error('Failed to load users:', err),
    });

    this.bookService.getAllBooks().subscribe({
      next: (books: BookResponse[]) => {
        // For reservations, show all books, availability is handled by backend logic for actual loan conversion
        this.bookOptions.set(
          books.map((b) => ({
            value: b.id,
            label: `${b.titre} (${b.nombreExemplaires} dispo)`,
          }))
        );
        this.loadingData.set(false);
      },
      error: (err) => console.error('Failed to load books:', err),
    });
  }

  onSave(): void {
    if (this.reservationForm.valid) {
      const formValue = { ...this.reservationForm.getRawValue() }; // getRawValue for disabled fields
      if (formValue.reservationDate) {
        formValue.reservationDate = formValue.reservationDate
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
