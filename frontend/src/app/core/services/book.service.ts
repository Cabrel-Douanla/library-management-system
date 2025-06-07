// src/app/core/services/book.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { BookRequest, BookResponse, AuthorRequest, AuthorResponse, BookStockUpdateRequest } from '../models/book.interface';

@Injectable({
  providedIn: 'root'
})
export class BookService {
  private bookApiUrl = `${environment.apiUrl}/books`;
  private authorApiUrl = `${environment.apiUrl}/authors`;

  constructor(private http: HttpClient) { }

  // --- Book Operations ---
  getAllBooks(): Observable<BookResponse[]> {
    return this.http.get<BookResponse[]>(this.bookApiUrl);
  }

  getBookById(id: string): Observable<BookResponse> {
    return this.http.get<BookResponse>(`${this.bookApiUrl}/${id}`);
  }

  createBook(book: BookRequest): Observable<BookResponse> {
    return this.http.post<BookResponse>(this.bookApiUrl, book);
  }

  updateBook(id: string, book: BookRequest): Observable<BookResponse> {
    return this.http.put<BookResponse>(`${this.bookApiUrl}/${id}`, book);
  }

  deleteBook(id: string): Observable<void> {
    return this.http.delete<void>(`${this.bookApiUrl}/${id}`);
  }

  updateBookStock(id: string, stockUpdate: BookStockUpdateRequest): Observable<BookResponse> {
    return this.http.patch<BookResponse>(`${this.bookApiUrl}/${id}/stock`, stockUpdate);
  }

  // --- Author Operations ---
  getAllAuthors(): Observable<AuthorResponse[]> {
    return this.http.get<AuthorResponse[]>(this.authorApiUrl);
  }

  getAuthorById(id: string): Observable<AuthorResponse> {
    return this.http.get<AuthorResponse>(`${this.authorApiUrl}/${id}`);
  }

  createAuthor(author: AuthorRequest): Observable<AuthorResponse> {
    return this.http.post<AuthorResponse>(this.authorApiUrl, author);
  }

  updateAuthor(id: string, author: AuthorRequest): Observable<AuthorResponse> {
    return this.http.put<AuthorResponse>(`${this.authorApiUrl}/${id}`, author);
  }

  deleteAuthor(id: string): Observable<void> {
    return this.http.delete<void>(`${this.authorApiUrl}/${id}`);
  }
}
