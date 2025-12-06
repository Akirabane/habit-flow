import { Injectable, signal, computed } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class SessionStore {

  // --- TOKEN ---
  private readonly tokenSignal = signal<string | null>(localStorage.getItem('token'));

  readonly token = computed(() => this.tokenSignal());

  readonly isLoggedIn = computed(() => !!this.tokenSignal());

  // --- USER INFO (extrait depuis le JWT) ---
  private readonly userSignal = signal<any | null>(null);
  readonly user = computed(() => this.userSignal());

  constructor() {
    const token = this.tokenSignal();
    if (token) this.loadUserFromToken(token);
  }

  setToken(token: string) {
    this.tokenSignal.set(token);
    localStorage.setItem('token', token);
    this.loadUserFromToken(token);
  }

  clear() {
    this.tokenSignal.set(null);
    this.userSignal.set(null);
    localStorage.removeItem('token');
  }

  private loadUserFromToken(token: string) {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      this.userSignal.set(payload);
    } catch {
      this.clear();
    }
  }
}
