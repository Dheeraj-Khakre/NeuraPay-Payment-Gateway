import { Component, inject } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { NgIf } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  standalone: true,
  imports: [ReactiveFormsModule, NgIf, RouterLink],
  template: `<div class="auth-page"><section class="auth-showcase"><div class="auth-logo">N</div><div><h1>Payments,<br/>under control.</h1><p>Monitor, process and reconcile every transaction in one secure workspace.</p></div></section><section class="auth-card"><div class="mobile-logo">NeuraPay</div><h2>Welcome back</h2><p class="sub">Sign in to your merchant dashboard</p><form [formGroup]="form" (ngSubmit)="submit()"><label>Email address<input type="email" formControlName="email" placeholder="you@company.com"></label><label>Password<input type="password" formControlName="password" placeholder="Enter your password"></label><p class="error" *ngIf="error">{{error}}</p><button [disabled]="form.invalid || loading">{{loading ? 'Signing in...' : 'Sign in'}} <span>→</span></button></form><p class="switch">New to NeuraPay? <a routerLink="/signup">Create an account</a></p></section></div>`,
  styles: [`*{box-sizing:border-box}.auth-page{min-height:100vh;display:grid;grid-template-columns:minmax(360px,46%) 1fr;background:#f7f8fc}.auth-showcase{background:radial-gradient(circle at 20% 20%,#7b69ef 0,#2939a8 28%,#10182f 68%);color:white;padding:64px;display:flex;flex-direction:column;justify-content:space-between}.auth-logo,.mobile-logo{font-weight:900}.auth-logo{width:46px;height:46px;border-radius:14px;background:white;color:#596be0;display:grid;place-items:center}.auth-showcase h1{font-size:64px;line-height:1.05;margin:0 0 20px}.auth-showcase p{max-width:430px;color:#cbd3ee;font-size:17px}.auth-card{width:min(420px,80%);margin:auto}.auth-card h2{font-size:34px;margin:0}.sub{color:#7c8499;margin:8px 0 32px}.auth-card label{display:grid;gap:8px;font-size:13px;font-weight:700;margin-top:18px}.auth-card input{height:48px;border:1px solid #dde1eb;border-radius:10px;padding:0 14px}.auth-card button{width:100%;height:50px;margin-top:24px;border:0;border-radius:10px;background:#6576df;color:white;font-weight:800}.auth-card button:disabled{opacity:.6}.error{color:#c33;background:#fff0f2;padding:10px;border-radius:8px}.switch{text-align:center;color:#7c8499}.switch a{color:#596dcf}.mobile-logo{display:none}@media(max-width:780px){.auth-page{grid-template-columns:1fr}.auth-showcase{display:none}.auth-card{width:calc(100% - 40px)}.mobile-logo{display:block;color:#6576df;font-size:20px;margin-bottom:54px}}`]
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);
  form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]]
  });
  loading = false;
  error = '';

  submit() {
    if (this.form.invalid) return;
    this.loading = true;
    const value = this.form.getRawValue();
    this.auth.login(value.email, value.password).subscribe({
      next: response => {
        this.loading = false;
        this.auth.saveSession(response.data);
        this.router.navigateByUrl('/');
      },
      error: error => {
        this.loading = false;
        this.error = error.error?.message || 'Invalid email or password';
      }
    });
  }
}
