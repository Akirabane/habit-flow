import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { SessionStore } from './session.store';

export const authGuard: CanActivateFn = () => {

  const session = inject(SessionStore);
  const router = inject(Router);

  if (!session.isLoggedIn()) {
    router.navigate(['/login']);
    return false;
  }
  return true;
};
