import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EtudiantService {

  apiUrl = 'http://localhost:8080/api/etudiants';

  constructor(private http: HttpClient) { }

  validerEtudiant(data: { email: string; numApogee: string; cin: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/valider`, data);
  }

  envoyerDemande(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/demande`, data);
  }
}
