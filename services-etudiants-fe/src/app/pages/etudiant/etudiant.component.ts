import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, Validators } from '@angular/forms';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { InputGroupModule } from 'primeng/inputgroup';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-etudiant',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    HttpClientModule,
    InputGroupModule,
    InputTextModule,
    InputNumberModule,
    ButtonModule,
  ],
  templateUrl: './etudiant.component.html',
  styleUrls: ['./etudiant.component.css'],
})
export class EtudiantComponent implements OnInit {
  email = '';
  numApogee: number | null = null;

  cin = '';

  infosValid = false;
  erreur = '';

  typeDemande = '';
  niveauReleve: string = '';
  filiereReleve: string = '';
  niveauReussite: string = '';
  filiereReussite: string = '';

  stageData: any = {
    type_stage: '',
  };

  verifyForm: FormGroup;
  verified = false;
  submitted = false;
  isChecking = false;
  private verifyTimer: any = null;
  allNiveaux = ['1ère année', '2ème année', '3ème année'];
  successMessage: string = '';
  errorMessage: string = '';

  constructor(private http: HttpClient, private fb: FormBuilder) {
    this.verifyForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      numApogee: [null, Validators.required],
      CIN: ['', Validators.required],
    });
  }

  ngOnInit(): void {}

  onFieldsChange() {
    this.erreur = '';
    if (!this.canAutoVerify()) {
      return;
    }
    clearTimeout(this.verifyTimer);
    this.verifyTimer = setTimeout(() => this.verifierEtudiant(), 400);
  }

  private canAutoVerify(): boolean {
    const emailOk = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.email || '');
    const apogeeOk =
      this.numApogee !== null &&
      this.numApogee !== undefined &&
      /^\d+$/.test(String(this.numApogee));
    const cinOk = !!this.cin && this.cin.trim().length >= 3;

    return emailOk && apogeeOk && cinOk;
  }

  getNiveauxDisponibles(filiere: string): string[] {
    if (filiere === '2AP') {
      return ['1ère année', '2ème année'];
    } else if (['GI', 'GSTR', 'GM', 'SCM', 'GC'].includes(filiere)) {
      return ['1ère année', '2ème année', '3ème année'];
    } else {
      return [];
    }
  }

  verifierEtudiant() {
    console.log('Valeurs saisies :');
    console.log('Email:', this.email);
    console.log('Num Apogee:', this.numApogee);
    console.log('CIN:', this.cin);

    if (!this.canAutoVerify()) {
      return;
    }
    this.isChecking = true;

    console.log('Tous les champs sont remplis ✅, envoi de la requête...');

    this.http
      .post<{ valide: boolean }>('http://localhost:8080/api/etudiants/valider', {
        email: this.email,
        numApogee: this.numApogee,
        cin: this.cin,
      })
      .subscribe({
        next: (res) => {
          console.log('Réponse du serveur :', res);
          this.isChecking = false;
          if (res?.valide) {
            this.infosValid = true;
            this.erreur = '';
          } else {
            this.erreur = 'Informations invalides !';
          }
        },
        error: (err) => {
          console.error('Erreur serveur :', err);
          this.erreur = 'Erreur serveur';
        },
      });
  }

  verifierEtudiantAuto() {
    if (this.email && this.numApogee && this.cin) {
      this.verifierEtudiant();
    }
  }

  choisirType(type: string) {
    this.typeDemande = type;
  }

  envoyerForm() {
    let data: any = { numApogee: Number(this.numApogee) };

    switch (this.typeDemande) {
      case 'releve':
        data.niveau = this.niveauReleve;
        data.filiere = this.filiereReleve;
        break;
      case 'stage':
        data = { ...data, ...this.stageData };
        break;
      case 'reussite':
        data.niveau = this.niveauReussite;
        data.filiere = this.filiereReussite;
        break;
    }

    this.http
      .post(`http://localhost:8080/api/etudiants/demandes/${this.typeDemande}`, data, {
        headers: { 'Content-Type': 'application/json' },
      })
      .subscribe({
        next: () => {
          this.successMessage = 'Demande envoyée avec succès !';
          setTimeout(() => this.successMessage = '', 5000);
          this.errorMessage = '';
        },
        error: (err) => {
          console.error(err);
          this.errorMessage = 'Une erreur est survenue. Veuillez réessayer.';
          this.successMessage = '';
        },
      });
  }
}
