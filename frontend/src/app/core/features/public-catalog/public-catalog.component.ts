import {
  Component,
  OnInit,
  signal,
  WritableSignal,
  computed,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatGridListModule } from '@angular/material/grid-list';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { BookCardComponent } from './components/book-card/book-card.component';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import {
  CatalogueEntry,
  NewArrival,
  Suggestion,
} from '../../models/catalog.interface';
import { PublicCatalogService } from '../../services/public-catalog.service';

@Component({
  selector: 'app-public-catalog',
  standalone: true,
  imports: [
    CommonModule,
    MatTabsModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    MatSelectModule,
    MatProgressBarModule,
    MatGridListModule,
    ReactiveFormsModule,
    BookCardComponent, // N'oubliez pas d'importer le sous-composant
  ],
  templateUrl: './public-catalog.component.html',
  styleUrls: ['./public-catalog.component.scss'],
})
export class PublicCatalogComponent implements OnInit {
  // Signals pour les données et l'état de chargement
  allCatalogueEntries = signal<CatalogueEntry[]>([]);
  newArrivals = signal<NewArrival[]>([]);
  suggestions = signal<Suggestion[]>([]);
  loading = signal(false);

  // Statistiques du catalogue
  totalBooksCount = computed(() => this.allCatalogueEntries().length);
  availableBooksCount = computed(
    () =>
      this.allCatalogueEntries().filter((b) => b.nombreExemplaires > 0).length
  );
  newArrivalsCount = computed(() => this.newArrivals().length);

  // Filtres et recherche
  searchControl = new FormControl('');
  selectedGenre = new FormControl('');
  availableGenres = signal<string[]>([]);
  selectedTabIndex = signal<number>(0); // 0: Catalogue, 1: Nouveautés, 2: Suggestions

  // Pour la responsivité de la grille
  cols = signal(4); // Default to 4 columns for larger screens
  rowHeight = signal('450px'); // Adjusted for the new card height

  // Computed signals pour filtrer les résultats
  filteredCatalogueEntries = computed(() => {
    const searchTerm = this.searchControl.value?.toLowerCase() || '';
    const genreFilter = this.selectedGenre.value;

    return this.allCatalogueEntries().filter((entry) => {
      const matchesSearch =
        entry.titre.toLowerCase().includes(searchTerm) ||
        entry.auteurNom.toLowerCase().includes(searchTerm) ||
        (entry.description &&
          entry.description.toLowerCase().includes(searchTerm)) ||
        (entry.isbn && entry.isbn.toLowerCase().includes(searchTerm));
      const matchesGenre = genreFilter ? entry.genre === genreFilter : true;
      return matchesSearch && matchesGenre;
    });
  });

  filteredNewArrivals = computed(() => {
    const searchTerm = this.searchControl.value?.toLowerCase() || '';
    return this.newArrivals().filter(
      (entry) =>
        entry.titre.toLowerCase().includes(searchTerm) ||
        entry.auteurNom.toLowerCase().includes(searchTerm)
    );
  });

  filteredSuggestions = computed(() => {
    const searchTerm = this.searchControl.value?.toLowerCase() || '';
    return this.suggestions().filter(
      (entry) =>
        entry.titre.toLowerCase().includes(searchTerm) ||
        entry.auteurNom.toLowerCase().includes(searchTerm)
    );
  });

  constructor(
    private publicCatalogService: PublicCatalogService,
    private breakpointObserver: BreakpointObserver
  ) // private dialog: MatDialog // Si vous voulez une modale pour les détails
  {
    // Adapter le nombre de colonnes de la grille en fonction de la taille de l'écran
    this.breakpointObserver
      .observe([
        Breakpoints.XSmall,
        Breakpoints.Small,
        Breakpoints.Medium,
        Breakpoints.Large,
        Breakpoints.XLarge,
      ])
      .subscribe((result) => {
        if (result.matches) {
          if (result.breakpoints[Breakpoints.XSmall]) {
            this.cols.set(1);
          } else if (result.breakpoints[Breakpoints.Small]) {
            this.cols.set(2);
          } else if (result.breakpoints[Breakpoints.Medium]) {
            this.cols.set(3);
          } else {
            this.cols.set(4);
          }
        }
      });
  }

  ngOnInit(): void {
    this.availableGenres.set([
      'Fiction',
      'Science-Fiction',
      'Fantaisie',
      'Histoire',
      'Biographie',
      'Informatique',
      'Général',
      'Thriller',
      'Romance',
    ]);

    this.searchControl.valueChanges
      .pipe(debounceTime(300), distinctUntilChanged())
      .subscribe(() => {
        // Filtrage automatique via computed signals
      });

    this.selectedGenre.valueChanges.subscribe(() => {
      // Filtrage automatique via computed signals
    });

    this.loadAllData();
  }

  loadAllData(): void {
    this.loading.set(true);
    this.publicCatalogService.getAllCatalogueEntries().subscribe({
      next: (data) => {
        this.allCatalogueEntries.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Failed to load catalogue entries:', err);
        this.loading.set(false);
      },
    });

    this.publicCatalogService.getAllNewArrivals().subscribe({
      next: (data) => this.newArrivals.set(data),
      error: (err) => console.error('Failed to load new arrivals:', err),
    });

    this.publicCatalogService.getAllSuggestions().subscribe({
      next: (data) => this.suggestions.set(data),
      error: (err) => console.error('Failed to load suggestions:', err),
    });
  }

  // Gérer le changement d'onglet
  onTabChange(event: any): void {
    this.selectedTabIndex.set(event.index);
    // Vous pouvez déclencher un rechargement spécifique si les données de chaque onglet sont très différentes
    // ou si vous voulez vider le champ de recherche à chaque changement d'onglet
  }

  onRequestLoan(bookId: string): void {
    // Logique pour gérer la demande d'emprunt/réservation
    // Cela pourrait ouvrir une modale de confirmation
    alert(
      "Demande d'emprunt/réservation pour le livre ID: " +
        bookId +
        '. (Fonctionnalité à implémenter)'
    );
  }
}
