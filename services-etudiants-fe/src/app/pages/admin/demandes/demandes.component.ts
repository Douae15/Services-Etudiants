import { Component, OnInit } from '@angular/core';
import { DemandeService } from '../../../services/demandes.service';
import { ActivatedRoute, Router, RouterLink, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-demandes',
  standalone: true,
  imports: [RouterLink, CommonModule, RouterModule],
  templateUrl: './demandes.component.html',
  styleUrls: ['./demandes.component.css']
})
export class DemandesComponent implements OnInit {
  demandes: any[] = [];
  type: string = '';
  searchNumApogee: string = '';
  selectedTypeStage: string = '';
  isHistorique = false;
  selectedStatut: string = '';
  filters: any = {};
  message: string = '';


  constructor(
    private route: ActivatedRoute,
    private demandeService: DemandeService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.type = params.get('type') || '';
      this.isHistorique = this.route.snapshot.url.some(u => u.path === 'historique');
      this.loadDemandes();
    });
  }

  getTypeLabel(type: string): string {
    switch(type) {
      case 'attest_reussite': return "D'ATTESTATION DE RÉUSSITE";
      case 'attest_scolarite': return "D'ATTESTATION DE SCOLARITÉ";
      case 'convention_stage': return "DE CONVENTIONS DE STAGE";
      case 'releve_notes': return "DE RELEVÉS DE NOTES";
      default: return "";
    }
  }

  loadDemandes() {
    if (this.isHistorique) {
      this.demandeService.getAll(this.type).subscribe({
        next: (data) => {
          console.log('Historique reçu:', data);
          this.demandes = data;
        },
        error: (err) => console.error(err)
      });
    } else {
      this.demandeService.getDemandes(this.type, 'non traitée').subscribe({
        next: (data) => {
          console.log('Demandes non traitées reçues:', data);
          this.demandes = data;
        },
        error: (err) => console.error(err)
      });
    }
  }

  updateStatut(id: number, statut: string) {
    this.demandeService.updateStatut(this.type, id, statut).subscribe({
      next: () => {this.loadDemandes();
        this.message = `L'email concernant la demande ${statut} est envoyé.`;
        setTimeout(() => this.message = '', 5000);
      },
      error: (err) => console.error(err)
    });
  }

  onSearch(event: Event) {
    const target = event.target as HTMLInputElement;
    this.searchNumApogee = target.value;
  }

  onTypeStageFilter(event: Event) {
    const select = event.target as HTMLSelectElement;
    this.selectedTypeStage = select.value;
  }


  onStatutFilter(event: Event) {
    const select = event.target as HTMLSelectElement;
    this.selectedStatut = select.value;
  }

  applyFilters() {

  }


  get filteredDemandes() {
    let result = this.demandes;

    if (this.searchNumApogee) {
      result = result.filter(d =>
        d.numApogee != null && d.numApogee.toString().includes(this.searchNumApogee)
      );
    }

    if (this.selectedTypeStage) {
      result = result.filter(d => d.typeStage.toLowerCase() === this.selectedTypeStage.toLowerCase());
    }

    if (this.selectedStatut) {
      result = result.filter(d => d.statut.toLowerCase() === this.selectedStatut.toLowerCase());
    }

    if (this.isHistorique) {
      result = result.filter(d => d.statut === 'acceptée' || d.statut === 'refusée');
    }

    return result;
  }


  downloadPdf(type: string, id: number) {
    this.demandeService.downloadPdf(type, id).subscribe((blob) => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `${type}-${id}.pdf`;
      a.click();
      window.URL.revokeObjectURL(url);
    });
  }

  resendEmail(type: string, id: number, statut: string) {
    this.demandeService.resendEmail(type, id).subscribe({
      next: () => {
        this.message = "L'email concernant la demande est renvoyé.";
        setTimeout(() => this.message = '', 5000);
        if (statut === 'refusée') {
          this.updateStatut(id, 'acceptée');
        } else {
          this.loadDemandes();
        }
      },
      error: (err) => console.error(err)
    });
  }

  logout() {
    localStorage.removeItem('adminToken');
    sessionStorage.clear();
    this.router.navigate(['/home']);
  }
}
