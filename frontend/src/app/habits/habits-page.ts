import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../core/auth/auth.service';

@Component({
  selector: 'app-habits-page',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page">
      <header class="header">
        <div>
          <h1>HabitFlow</h1>
          <p *ngIf="user">
            Connecté en tant que {{ user.fullName }} ({{ user.email }})
          </p>
        </div>
        <button (click)="logout()">Se déconnecter</button>
      </header>

      <main>
        <p>
          Ici, on affichera la liste des habitudes, les stats, les boutons de
          check, etc.
        </p>
      </main>
    </div>
  `,
})
export class HabitsPageComponent {
  user = this['authService'].getUser();

  constructor(private authService: AuthService, private router: Router) {}

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
