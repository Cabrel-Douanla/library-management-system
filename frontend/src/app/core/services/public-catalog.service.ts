import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { CatalogueEntry, NewArrival, Suggestion } from '../models/catalog.interface';

@Injectable({
  providedIn: 'root'
})
export class PublicCatalogService {
  private publicApiUrl = `${environment.apiUrl}/public`;

  constructor(private http: HttpClient) { }

  getAllCatalogueEntries(): Observable<CatalogueEntry[]> {
    return this.http.get<CatalogueEntry[]>(`${this.publicApiUrl}/catalogue`);
  }

  getCatalogueEntryById(id: string): Observable<CatalogueEntry> {
    return this.http.get<CatalogueEntry>(`${this.publicApiUrl}/catalogue/${id}`);
  }

  getAllNewArrivals(): Observable<NewArrival[]> {
    return this.http.get<NewArrival[]>(`${this.publicApiUrl}/nouveautes`);
  }

  getAllSuggestions(category?: string): Observable<Suggestion[]> {
    let params = new HttpParams();
    if (category) {
      params = params.set('category', category);
    }
    return this.http.get<Suggestion[]>(`${this.publicApiUrl}/suggestions`, { params });
  }

  // Si vous ajoutez des fonctionnalités d'admin pour gérer les suggestions depuis le frontend
  createSuggestion(bookId: string, category: string, priority: number): Observable<Suggestion> {
    const params = new HttpParams()
      .set('bookId', bookId)
      .set('category', category)
      .set('priority', priority.toString());
    return this.http.post<Suggestion>(`${this.publicApiUrl}/suggestions`, null, { params });
  }

  deleteSuggestion(id: string): Observable<void> {
    return this.http.delete<void>(`${this.publicApiUrl}/suggestions/${id}`);
  }
}
