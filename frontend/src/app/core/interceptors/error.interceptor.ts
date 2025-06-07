import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';
import { inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../services/auth.service'; // Pour le logout en cas de 401

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const snackBar = inject(MatSnackBar);
  const authService = inject(AuthService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'An unknown error occurred!';
      if (error.error instanceof ErrorEvent) {
        // Erreur côté client
        errorMessage = `Error: ${error.error.message}`;
      } else {
        // Erreur côté serveur
        if (error.status === 401) {
          errorMessage = 'Unauthorized: Please log in again.';
          snackBar.open(errorMessage, 'Fermer', { duration: 3000 });
          authService.logout(); // Déconnexion automatique en cas de 401
        } else if (error.status === 403) {
          errorMessage = 'Forbidden: You do not have permission to access this resource.';
        } else if (error.status === 404) {
          errorMessage = 'Resource not found.';
        } else if (error.status === 400 && error.error.errors) {
          // Erreurs de validation du backend (ProblemDetail)
          errorMessage = Object.values(error.error.errors).join(', ');
        } else if (error.error && error.error.detail) { // Pour le format ProblemDetail
            errorMessage = error.error.detail;
        }
        else {
          errorMessage = `Server Error (${error.status}): ${error.message}`;
        }
      }
      snackBar.open(errorMessage, 'Fermer', { duration: 5000 });
      return throwError(() => new Error(errorMessage));
    })
  );
};
