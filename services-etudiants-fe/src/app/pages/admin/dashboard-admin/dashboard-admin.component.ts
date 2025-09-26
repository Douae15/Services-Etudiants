import { Component } from '@angular/core';
import { Router, RouterLink, RouterModule } from '@angular/router';
import { DemandesComponent } from '../demandes/demandes.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard-admin',
  standalone: true,
  imports: [RouterLink, CommonModule, RouterModule, DemandesComponent],
  templateUrl: './dashboard-admin.component.html',
  styleUrls: ['./dashboard-admin.component.css']

})
export class DashboardAdminComponent {

  constructor(private router: Router) {}

  logout() {
    localStorage.removeItem('adminToken');
    sessionStorage.clear();
    this.router.navigate(['/home']);
  }
}
