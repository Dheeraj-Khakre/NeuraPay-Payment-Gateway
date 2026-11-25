import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { ApiResponse, AuthResponse, MerchantSignUpRequest, MerchantResponse } from '../models/api.models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  readonly isAuthenticated = signal(!!sessionStorage.getItem('neurapay.token'));
  readonly session = signal<AuthResponse | null>(this.readSession());

  login(email: string, password: string) {
    return this.http.post<ApiResponse<AuthResponse>>(`${environment.apiBaseUrl}/api/v1/auth/login`, { email, password });
  }
  signup(payload: MerchantSignUpRequest) {
    return this.http.post<ApiResponse<MerchantResponse>>(`${environment.apiBaseUrl}/api/v1/auth/signup`, payload);
  }
  saveSession(auth: AuthResponse) {
    sessionStorage.setItem('neurapay.token', auth.accessToken);
    sessionStorage.setItem('neurapay.session', JSON.stringify(auth));
    this.session.set(auth);
    this.isAuthenticated.set(true);
  }
  logout() {
    sessionStorage.removeItem('neurapay.token');
    sessionStorage.removeItem('neurapay.session');
    this.session.set(null);
    this.isAuthenticated.set(false);
    this.router.navigateByUrl('/login');
  }
  private readSession(): AuthResponse | null {
    try { return JSON.parse(sessionStorage.getItem('neurapay.session') || 'null'); } catch { return null; }
  }
}
