import { Component, inject } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { NgFor, NgIf } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  standalone: true,
  imports: [ReactiveFormsModule, NgFor, NgIf, RouterLink],
  template: `<div class="auth-page"><section class="auth-card"><div class="brand">NeuraPay</div><h2>Create merchant account</h2><p>Start accepting payments securely.</p><form [formGroup]="form" (ngSubmit)="submit()"><div class="grid"><label>Business name<input formControlName="businessName"></label><label>Legal name<input formControlName="name"></label><label>Business type<select formControlName="businessType"><option *ngFor="let type of types" [value]="type">{{type}}</option></select></label><label>Phone<input formControlName="phone"></label><label>Email<input type="email" formControlName="email"></label><label>Password<input type="password" formControlName="password"></label></div><p class="error" *ngIf="error">{{error}}</p><button [disabled]="form.invalid || loading">{{loading ? 'Creating...' : 'Create account'}} →</button></form><p class="switch">Already registered? <a routerLink="/login">Sign in</a></p></section></div>`,
  styles: [`.auth-page{min-height:100vh;display:grid;place-items:center;background:radial-gradient(circle at 20% 10%,#dfe5ff,transparent 35%),#f7f8fc}.auth-card{width:min(720px,calc(100% - 32px));background:white;border:1px solid #e7e9f1;border-radius:18px;padding:38px;box-shadow:0 20px 50px #1e293b12}.brand{color:#5d70d8;font-weight:900}.auth-card h2{font-size:32px;margin:18px 0 4px}.auth-card>p{color:#81889a}.grid{display:grid;grid-template-columns:1fr 1fr;gap:16px;margin-top:24px}.grid label{display:grid;gap:7px;font-size:13px;font-weight:700}.grid input,.grid select{height:46px;border:1px solid #dde1eb;border-radius:9px;padding:0 12px}button{width:100%;height:48px;border:0;border-radius:9px;background:#6576df;color:white;font-weight:800;margin-top:22px}.error{color:#c33}.switch{text-align:center}@media(max-width:700px){.grid{grid-template-columns:1fr}.auth-card{padding:24px}}`]
})
export class SignupComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);
  types = ['LLP','PROPRIETORSHIP','PRIVATE_LIMITED','PUBLIC_LIMITED','SOLE_PROPRIETORSHIP','PARTNERSHIP','HUF','TRUST','NGO'];
  form = this.fb.nonNullable.group({
    businessName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(150)]],
    businessType: ['PRIVATE_LIMITED', Validators.required],
    name: ['', [Validators.required, Validators.maxLength(150)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(100)]],
    phone: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(20)]]
  });
  loading = false;
  error = '';

  submit() {
    if (this.form.invalid) return;
    this.loading = true;
    this.auth.signup(this.form.getRawValue()).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigateByUrl('/login');
      },
      error: error => {
        this.loading = false;
        this.error = error.error?.message || 'Unable to create account';
      }
    });
  }
}
