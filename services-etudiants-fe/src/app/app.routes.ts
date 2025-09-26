import { Routes } from '@angular/router';
import { EtudiantComponent } from './pages/etudiant/etudiant.component';
import { LoginAdminComponent } from './pages/admin/login-admin/login-admin.component';
import { DashboardAdminComponent } from './pages/admin/dashboard-admin/dashboard-admin.component';
import { HomeComponent } from './pages/home/home.component';
import { DemandesComponent } from './pages/admin/demandes/demandes.component';
import { AuthGuard } from './guard/auth.guard';

export const routes: Routes = [
  {
    path: 'home',
    component: HomeComponent,
  },
  {
    path: 'etudiant',
    component: EtudiantComponent,
  },
  { path: 'admin', component: LoginAdminComponent},
  { path: 'admin/dashboard', component: DashboardAdminComponent, canActivate: [AuthGuard] },
  {
    path: 'admin/demandes/:type',
    component: DemandesComponent
  },
  { path: 'admin/historique/:type', component: DemandesComponent },
  {
    path: '**',
    redirectTo: 'home',
  },
];
