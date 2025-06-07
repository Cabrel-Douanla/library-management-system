import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import {
  LoanResponse,
  LoanRequest,
  LoanStatus,
  ReturnResponse,
  ReturnRequest,
  ReturnStatus,
  ReservationResponse,
  ReservationRequest,
  ReservationStatus,
} from '../models/loans.interface';

@Injectable({
  providedIn: 'root',
})
export class LoanService {
  private loanApiUrl = `${environment.apiUrl}/loans`;
  private reservationApiUrl = `${environment.apiUrl}/reservations`;
  private returnApiUrl = `${environment.apiUrl}/returns`;

  constructor(private http: HttpClient) {}

  // --- Loan Operations ---
  getAllLoans(): Observable<LoanResponse[]> {
    return this.http.get<LoanResponse[]>(this.loanApiUrl);
  }

  getLoansByUserId(userId: string): Observable<LoanResponse[]> {
    return this.http.get<LoanResponse[]>(`${this.loanApiUrl}/user/${userId}`);
  }

  createLoan(loan: LoanRequest): Observable<LoanResponse> {
    return this.http.post<LoanResponse>(this.loanApiUrl, loan);
  }

  updateLoanStatus(id: string, status: LoanStatus): Observable<LoanResponse> {
    const params = new HttpParams().set('status', status);
    return this.http.put<LoanResponse>(
      `${this.loanApiUrl}/${id}/status`,
      null,
      { params }
    );
  }

  deleteLoan(id: string): Observable<void> {
    return this.http.delete<void>(`${this.loanApiUrl}/${id}`);
  }

  // --- Return Operations ---
  getAllReturns(): Observable<ReturnResponse[]> {
    return this.http.get<ReturnResponse[]>(this.returnApiUrl);
  }

  recordReturn(returnReq: ReturnRequest): Observable<ReturnResponse> {
    return this.http.post<ReturnResponse>(this.returnApiUrl, returnReq);
  }

  updateReturnStatus(
    id: string,
    status: ReturnStatus
  ): Observable<ReturnResponse> {
    const params = new HttpParams().set('status', status);
    return this.http.put<ReturnResponse>(
      `${this.returnApiUrl}/${id}/status`,
      null,
      { params }
    );
  }

  deleteReturn(id: string): Observable<void> {
    return this.http.delete<void>(`${this.returnApiUrl}/${id}`);
  }

  // --- Reservation Operations ---
  getAllReservations(): Observable<ReservationResponse[]> {
    return this.http.get<ReservationResponse[]>(this.reservationApiUrl);
  }

  getReservationsByUserId(userId: string): Observable<ReservationResponse[]> {
    return this.http.get<ReservationResponse[]>(
      `${this.reservationApiUrl}/user/${userId}`
    );
  }

  createReservation(
    reservation: ReservationRequest
  ): Observable<ReservationResponse> {
    return this.http.post<ReservationResponse>(
      this.reservationApiUrl,
      reservation
    );
  }

  updateReservationStatus(
    id: string,
    status: ReservationStatus
  ): Observable<ReservationResponse> {
    const params = new HttpParams().set('status', status);
    return this.http.put<ReservationResponse>(
      `${this.reservationApiUrl}/${id}/status`,
      null,
      { params }
    );
  }

  convertReservationToLoan(id: string): Observable<LoanResponse> {
    return this.http.patch<LoanResponse>(
      `${this.reservationApiUrl}/${id}/convert-to-loan`,
      null
    );
  }

  deleteReservation(id: string): Observable<void> {
    return this.http.delete<void>(`${this.reservationApiUrl}/${id}`);
  }
}
