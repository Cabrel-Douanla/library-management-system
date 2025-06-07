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
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogContent, MatDialogActions } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { MatDatepickerActions, MatDatepickerModule } from '@angular/material/datepicker';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { DialogResult, DialogData } from '../../../../models/dialog.interface';
import { SelectOption } from '../../../../models/form-options.interface';
import {
  ReturnStatus,
  ReturnRequest,
  ReturnResponse,
  LoanResponse,
  LoanStatus,
} from '../../../../models/loans.interface';
import { LoanService } from '../../../../services/loan.service';

@Component({
  selector: 'app-return-form-dialog',
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
    MatDialogContent,
    MatDialogActions,
  ],
  templateUrl: './return-form-dialog.component.html',
  styleUrls: ['./return-form-dialog.component.scss'],
})
export class ReturnFormDialogComponent implements OnInit {
  returnForm: FormGroup;
  title: string;
  mode: 'create' | 'edit';
  loadingData = signal(false);

  loanOptions: WritableSignal<SelectOption[]> = signal([]);
  returnStatusOptions: WritableSignal<SelectOption[]> = signal(
    Object.values(ReturnStatus).map((status) => ({
      value: status,
      label: status.replace(/_/g, ' '),
    }))
  );

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<
      ReturnFormDialogComponent,
      DialogResult<ReturnRequest>
    >,
    @Inject(MAT_DIALOG_DATA)
    public data: DialogData<
      ReturnResponse & { loanDetails: LoanResponse | null }
    >, // Pass loan details for context
    private loanService: LoanService
  ) {
    this.title = data.title;
    this.mode = data.mode;

    this.returnForm = this.fb.group({
      loanId: ['', Validators.required],
      effectiveReturnDate: [new Date(), Validators.required],
      status: [ReturnStatus.TRAITÉ, Validators.required], // Default status
    });

    if (this.mode === 'create' && this.data.data.loanDetails) {
      // Pre-fill loanId if coming from a specific loan
      this.returnForm.get('loanId')?.setValue(this.data.data.loanDetails.id);
      this.returnForm.get('loanId')?.disable(); // Disable if pre-filled
    }

    // Edit mode (not typical for returns, usually created then status updated)
    if (this.mode === 'edit' && this.data.data) {
      const ret = this.data.data;
      this.returnForm.patchValue({
        loanId: ret.loanId,
        effectiveReturnDate: ret.effectiveReturnDate
          ? new Date(ret.effectiveReturnDate)
          : null,
        status: ret.status,
      });
      this.returnForm.get('loanId')?.disable(); // Loan ID should not change
    }
  }

  ngOnInit(): void {
    this.loadLoans();
  }

  loadLoans(): void {
    this.loadingData.set(true);
    this.loanService.getAllLoans().subscribe({
      next: (loans: LoanResponse[]) => {
        this.loanOptions.set(
          loans
            .filter(
              (loan) =>
                loan.status === LoanStatus.EN_COURS ||
                (this.mode === 'edit' && this.data.data?.loanId === loan.id)
            ) // Only active loans for new returns
            .map((l) => ({
              value: l.id,
              label: `Emprunt ID: ${l.id.slice(
                0,
                8
              )}... (Livre: ${l.bookId.slice(0, 8)}..., User: ${l.userId.slice(
                0,
                8
              )}...)`,
            }))
        );
        this.loadingData.set(false);
      },
      error: (err) => console.error('Failed to load loans:', err),
    });
  }

  onSave(): void {
    if (this.returnForm.valid) {
      const formValue = { ...this.returnForm.getRawValue() };
      if (formValue.effectiveReturnDate) {
        formValue.effectiveReturnDate = formValue.effectiveReturnDate
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
