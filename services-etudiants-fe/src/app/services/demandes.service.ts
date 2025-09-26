import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";

@Injectable({ providedIn: 'root' })
export class DemandeService {
  private apiUrl = 'http://localhost:8080/api/admin';

  constructor(private http: HttpClient) {}

  getDemandes(type: string, statut: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/demandes/${type}?statut=${statut}`);
}

getAll(type: string): Observable<any[]> {
  return this.http.get<any[]>(`${this.apiUrl}/demandes/${type}`);
}

updateStatut(type: string, id: number, statut: string): Observable<any> {
  return this.http.put(`${this.apiUrl}/demandes/${type}/${id}/statut?statut=${statut}`, {});
}

downloadPdf(type: string, id: number) {
  return this.http.get(`${this.apiUrl}/demandes/${type}/${id}/pdf`, {
    responseType: 'blob'
  });
}

resendEmail(type: string, id: number) {
  return this.http.post(
    `${this.apiUrl}/demandes/${type}/${id}/resend-email`,
    {},
    { responseType: 'text' }
  );
}




}
