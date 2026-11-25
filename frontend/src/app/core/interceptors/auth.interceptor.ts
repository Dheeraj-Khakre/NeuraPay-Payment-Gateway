import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../auth/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const token = sessionStorage.getItem('neurapay.token');
  const authorized = token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;
  return next(authorized).pipe(catchError((error: HttpErrorResponse) => {
    if (error.status === 401) auth.logout();
    return throwError(() => error);
  }));
};
