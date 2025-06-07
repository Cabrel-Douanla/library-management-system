import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { Role } from '../models/role.enum';
import { MatSnackBar } from '@angular/material/snack-bar';

export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const snackBar = inject(MatSnackBar);

  const requiredRoles = route.data['roles'] as Role[]; // Récupère les rôles requis des données de la route

  if (!requiredRoles || requiredRoles.length === 0) {
    return true; // Si aucun rôle n'est spécifié, autorise l'accès
  }

  if (authService.isLoggedIn() && authService.hasRole(requiredRoles)) {
    return true;
  } else {
    snackBar.open('Vous n\'avez pas les permissions nécessaires pour accéder à cette page.', 'Fermer', { duration: 5000 });
    router.navigate(['/dashboard']); // Redirige vers le tableau de bord ou une page d'accès refusé
    return false;
  }
};
