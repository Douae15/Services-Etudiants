import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AdminService } from '../../../services/admin.service';

@Component({
  selector: 'app-login-admin',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './login-admin.component.html',
  styleUrls: ['./login-admin.component.css'],
})
export class LoginAdminComponent {
  email = '';
  password = '';
  error = '';

  constructor(private adminService: AdminService, private router: Router) {}

  onLogin() {
    this.adminService.login(this.email, this.password).subscribe({
      next: (res) => {
        if (res === 'Login success') {
          this.error = '';
          this.router.navigate(['/admin/dashboard']);
          localStorage.setItem('adminToken', 'connected');
        } else {
          this.error = 'Email ou mot de passe incorrect';
        }
      },
      error: () => {
        this.error = 'Email ou mot de passe incorrect.';
      },
    });
  }
}
