import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';
import { LoginComponent } from './core/features/auth/login/login.component';
import { RegisterComponent } from './core/features/auth/register/register.component';
import { DashboardComponent } from './core/features/dashboard/dashboard.component';
import { PublicCatalogComponent } from './core/features/public-catalog/public-catalog.component';

export const appRoutes: Routes = [
  { path: '', redirectTo: '/public/catalog', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'public/catalog', component: PublicCatalogComponent }, // Route pour le catalogue public
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [authGuard],
  },
  {
    path: 'admin/books',
    loadComponent: () =>
      import('./core/features/books/book-list/book-list.component').then(
        (m) => m.BookListComponent
      ),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ADMIN', 'BIBLIOTHÉCAIRE'] },
  },
  {
    path: 'loans',
    loadComponent: () =>
      import('./core/features/loans/loan-list/loan-list.component').then(
        (m) => m.LoanListComponent
      ),
    canActivate: [authGuard], // Peut être un ADHÉRENT
  },

  {
    path: 'admin/users',
    loadComponent: () =>
      import('./core/features/users/user-list/user-list.component').then(
        (m) => m.UserListComponent
      ),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ADMIN', 'BIBLIOTHÉCAIRE'] },
  },

  { path: '**', redirectTo: '/public/catalog' },
];
