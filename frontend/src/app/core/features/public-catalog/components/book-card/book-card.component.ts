import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router'; // Si vous voulez un bouton de détails qui navigue
import { CatalogueEntry } from '../../../../models/catalog.interface';
import { AuthService } from '../../../../services/auth.service';

@Component({
  selector: 'app-book-card',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    RouterLink // Ajoutez RouterLink
  ],
  templateUrl: './book-card.component.html',
  styleUrls: ['./book-card.component.scss']
})
export class BookCardComponent {
  @Input({ required: true }) book!: CatalogueEntry;
  @Output() requestLoan = new EventEmitter<string>(); // Émet l'ID du livre

  // Signals pour l'état de l'authentification/rôles
  isLoggedIn = this.authService.isLoggedIn;

  constructor(private authService: AuthService) { }

  onRequestLoan(): void {
    if (this.isLoggedIn()) {
      this.requestLoan.emit(this.book.bookId);
    } else {
      // Potentiellement rediriger vers la page de login ou montrer un message
      // L'intercepteur d'erreurs peut déjà le faire pour les appels API.
      // Mais pour un bouton qui n'appelle pas l'API, on peut le gérer ici.
    }
  }
}
