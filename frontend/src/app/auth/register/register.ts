import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.html',
})
export class RegisterComponent {
  fullName = '';
  email = '';
  password = '';
  loading = false;
  error: string | null = null;
  success: string | null = null;

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit(): void {
    this.error = null;
    this.success = null;
    this.loading = true;

    this.authService
      .register({
        fullName: this.fullName,
        email: this.email,
        password: this.password,
      })
      .subscribe({
        next: () => {
          this.loading = false;
          this.success = 'Compte créé ! Tu peux maintenant te connecter.';
          // Option : rediriger automatiquement :
          // this.router.navigate(['/login']);
        },
        error: (err) => {
          this.loading = false;
          if (err.status === 400 || err.status === 409) {
            this.error =
              err.error?.message ??
              "Impossible de créer le compte (email déjà utilisé ?).";
          } else {
            this.error = 'Erreur lors de la création du compte.';
          }
        },
      });
  }
}
