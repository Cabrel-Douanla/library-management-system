import { Component, Inject, OnInit, signal, WritableSignal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
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
import { LoanRequest, LoanResponse } from '../../../../models/loans.interface';
import { User } from '../../../../models/user.interface';
import { BookService } from '../../../../services/book.service';
import { UserService } from '../../../../services/user.service';

@Component({
  selector: 'app-loan-form-dialog',
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
  templateUrl: './loan-form.dialog.component.html',
  styleUrls: ['./loan-form.dialog.component.scss']
})
export class LoanFormDialogComponent implements OnInit {
  loanForm: FormGroup;
  title: string;
  mode: 'create' | 'edit';
  loadingData = signal(false);

  userOptions: WritableSignal<SelectOption[]> = signal([]);
  bookOptions: WritableSignal<SelectOption[]> = signal([]);

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<LoanFormDialogComponent, DialogResult<LoanRequest>>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData<LoanResponse>,
    private userService: UserService,
    private bookService: BookService
  ) {
    this.title = data.title;
    this.mode = data.mode;

    this.loanForm = this.fb.group({
      userId: ['', Validators.required],
      bookId: ['', Validators.required],
      loanDate: [new Date(), Validators.required],
    });

    if (this.mode === 'edit' && this.data.data) {
      const loan = this.data.data;
      this.loanForm.patchValue({
        userId: loan.userId,
        bookId: loan.bookId,
        loanDate: new Date(loan.loanDate),
      });
      // Disable userId and bookId if editing to prevent changing existing loan's core subject
      this.loanForm.get('userId')?.disable();
      this.loanForm.get('bookId')?.disable();
    }
  }

  ngOnInit(): void {
    this.loadUsersAndBooks();
  }

  loadUsersAndBooks(): void {
    this.loadingData.set(true);
    this.userService.getAllUsers().subscribe({
      next: (users: User[]) => {
        this.userOptions.set(users.map(u => ({ value: u.id, label: `${u.prenom} ${u.nom} (${u.email})` })));
        this.loadingData.set(false);
      },
      error: (err) => console.error('Failed to load users:', err)
    });

    this.bookService.getAllBooks().subscribe({
      next: (books: BookResponse[]) => {
        // Only show books with available copies for new loans
        this.bookOptions.set(books
          .filter(b => b.nombreExemplaires > 0 || (this.mode === 'edit' && this.data.data?.bookId === b.id)) // If editing, allow selecting the current book even if out of stock
          .map(b => ({ value: b.id, label: `${b.titre} (${b.nombreExemplaires} dispo)` })));
        this.loadingData.set(false);
      },
      error: (err) => console.error('Failed to load books:', err)
    });
  }

  onSave(): void {
    if (this.loanForm.valid) {
      const formValue = { ...this.loanForm.getRawValue() }; // Use getRawValue to include disabled fields
      if (formValue.loanDate) {
        formValue.loanDate = formValue.loanDate.toISOString().split('T')[0];
      }
      this.dialogRef.close({ action: 'save', data: formValue });
    }
  }

  onCancel(): void {
    this.dialogRef.close({ action: 'cancel' });
  }
}
