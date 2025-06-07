// src/app/core/models/dialog.interface.ts

export interface DialogData<T> {
  title: string;
  data?: T; // Data to be edited (optional for create)
  mode: 'create' | 'edit';
}

export interface DialogResult<T> {
  action: 'save' | 'cancel';
  data?: T;
}
