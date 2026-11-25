import { Component, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
  <div class="app-shell" [class.sidebar-collapsed]="collapsed()">
    <aside class="sidebar">
      <a class="brand" routerLink="/"><span class="brand-mark">N</span><span class="brand-name">NeuraPay</span></a>
      <nav>
        <a routerLink="/" routerLinkActive="active" [routerLinkActiveOptions]="{exact:true}"><span>⌂</span><b>Overview</b></a>
        <a routerLink="/orders" routerLinkActive="active"><span>▦</span><b>Orders</b></a>
        <a routerLink="/payments" routerLinkActive="active"><span>↗</span><b>Payments</b></a>
        <a routerLink="/api-keys" routerLinkActive="active"><span>⌘</span><b>API keys</b></a>
        <a routerLink="/vault" routerLinkActive="active"><span>◇</span><b>Card vault</b></a>
        <a routerLink="/audit" routerLinkActive="active"><span>◷</span><b>Audit logs</b></a>
      </nav>
      <div class="sidebar-foot"><a routerLink="/settings"><span>⚙</span><b>Settings</b></a></div>
    </aside>
    <main>
      <header class="topbar">
        <button class="icon-button" (click)="collapsed.set(!collapsed())" aria-label="Toggle sidebar">☰</button>
        <div class="search"><span>⌕</span><input placeholder="Search payments, orders..." /></div>
        <div class="top-actions"><button class="icon-button" (click)="toggleTheme()" aria-label="Toggle theme">◐</button><button class="icon-button">♧<i></i></button><button class="profile" (click)="auth.logout()"><span>{{ initials() }}</span><div><b>{{ auth.session()?.email || 'Merchant' }}</b><small>OWNER</small></div></button></div>
      </header>
      <section class="page-content"><router-outlet /></section>
    </main>
  </div>`,
  styles: [`
    :host{display:block}.app-shell{min-height:100vh;display:grid;grid-template-columns:248px minmax(0,1fr);transition:.25s}.sidebar-collapsed{grid-template-columns:78px minmax(0,1fr)}
    .sidebar{position:sticky;top:0;height:100vh;background:#0b1020;color:#aeb7d1;padding:22px 14px;display:flex;flex-direction:column;z-index:5}.brand{height:44px;display:flex;align-items:center;gap:10px;color:white;text-decoration:none;padding:0 8px;margin-bottom:24px}.brand-mark{width:36px;height:36px;border-radius:11px;display:grid;place-items:center;background:linear-gradient(135deg,#6b8cff,#8659e9);font-weight:800}.brand-name{font-size:20px;font-weight:800}.sidebar nav{display:grid;gap:6px}.sidebar a,.sidebar nav a{height:44px;border-radius:10px;color:#aeb7d1;text-decoration:none;display:flex;align-items:center;gap:13px;padding:0 12px}.sidebar nav a.active{background:linear-gradient(90deg,rgba(112,137,255,.22),rgba(112,137,255,.07));color:white}.sidebar a span{width:20px;text-align:center;font-size:18px}.sidebar-foot{margin-top:auto}.sidebar-collapsed .brand-name,.sidebar-collapsed b,.sidebar-collapsed .profile div{display:none}.sidebar-collapsed a{justify-content:center}.topbar{height:72px;background:var(--surface);border-bottom:1px solid var(--border);display:flex;align-items:center;gap:18px;padding:0 28px;position:sticky;top:0;z-index:4}.search{height:40px;width:min(420px,35vw);display:flex;align-items:center;gap:8px;background:var(--soft);border:1px solid var(--border);border-radius:10px;padding:0 12px}.search input{border:0;outline:0;background:transparent;width:100%;color:var(--text)}.top-actions{margin-left:auto;display:flex;align-items:center;gap:8px}.icon-button{width:40px;height:40px;border:1px solid var(--border);border-radius:10px;background:var(--surface);color:var(--text);position:relative}.icon-button i{position:absolute;width:7px;height:7px;border-radius:50%;background:#ff6b85;right:7px;top:7px}.profile{border:0;background:transparent;color:var(--text);display:flex;align-items:center;gap:9px;text-align:left}.profile>span{width:38px;height:38px;border-radius:50%;display:grid;place-items:center;background:#dfe7ff;color:#536fd6;font-weight:800}.profile div{display:grid}.profile small{color:var(--muted);margin-top:2px}.page-content{padding:28px;max-width:1500px;margin:auto}@media(max-width:900px){.app-shell,.sidebar-collapsed{grid-template-columns:78px minmax(0,1fr)}.brand-name,.sidebar b,.sidebar-foot b,.profile div{display:none}.sidebar{padding:18px 10px}.sidebar a,.sidebar nav a{justify-content:center}.search{display:none}.topbar{padding:0 16px}.page-content{padding:18px 14px}}
  `]
})
export class DashboardLayoutComponent {
  auth = inject(AuthService);
  collapsed = signal(false);
  initials = () => (this.auth.session()?.email || 'N').charAt(0).toUpperCase();
  toggleTheme() { document.body.classList.toggle('dark'); }
}
