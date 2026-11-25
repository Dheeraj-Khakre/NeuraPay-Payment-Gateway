import { Component, inject, OnInit, signal } from '@angular/core';
import { DatePipe, DecimalPipe, NgFor, NgIf } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { DomainService } from '../../core/services/domain.service';
import { PaymentResponse } from '../../core/models/api.models';
import { MoneyAmountPipe } from '../../shared/pipes/money-amount.pipe';

@Component({
  standalone: true,
  imports: [DatePipe, DecimalPipe, NgFor, NgIf, ReactiveFormsModule, MoneyAmountPipe],
  template: `
    <div class="heading"><div><p>Transactions</p><h1>Payments</h1></div><button (click)="initiating.set(!initiating())">{{initiating() ? 'Close' : 'Initiate payment'}}</button></div>
    <section class="create" *ngIf="initiating()">
      <h3>Initiate payment</h3>
      <form [formGroup]="form" (ngSubmit)="initiate()">
        <label>Order ID<input formControlName="orderId"></label>
        <label>Payment method<select formControlName="method" (change)="setMethodFields()"><option>CARD</option><option>UPI</option><option>WALLET</option><option>NETBANKING</option></select></label>
        <label *ngIf="method === 'CARD'">Vault token<input formControlName="token" placeholder="Token from Card vault"></label>
        <label *ngIf="method === 'UPI'">UPI VPA<input formControlName="vpa" placeholder="name@bank"></label>
        <label *ngIf="method === 'WALLET'">Wallet code<input formControlName="wallet" placeholder="Wallet provider"></label>
        <label *ngIf="method === 'NETBANKING'">Bank code<input formControlName="bankCode" placeholder="Bank code"></label>
        <button [disabled]="form.invalid || loading">{{loading ? 'Processing...' : 'Initiate'}}</button>
      </form>
      <p class="form-error" *ngIf="error">{{error}}</p>
      <small>Use a vault token for card payments. This form does not collect card numbers or CVV.</small>
    </section>
    <section class="panel">
      <div class="tr th"><span>Payment ID</span><span>Order</span><span>Amount</span><span>Method</span><span>Status</span><span>Created</span><span></span></div>
      <div class="tr" *ngFor="let payment of payments()">
        <span><b>{{payment.id.slice(0,8)}}…</b></span><span>{{payment.orderId.slice(0,8)}}…</span>
        <span>{{payment.amount | moneyAmount | number:'1.2-2'}} {{payment.amount.currency}}</span><span>{{payment.method}}</span>
        <span><i [attr.data-status]="payment.status">{{payment.status}}</i></span><span>{{payment.createdAt | date:'MMM d, HH:mm'}}</span>
        <button *ngIf="payment.status === 'AUTHORIZED' || payment.status === 'CREATED'" (click)="capture(payment)">Capture</button>
      </div>
      <div class="empty" *ngIf="loaded() && !payments().length">No payments found.</div>
    </section>`,
  styles: [`
    .heading{display:flex;justify-content:space-between}.heading p{color:var(--muted);margin:0}.heading h1{margin:4px 0 20px}.heading button,form button{border:0;border-radius:9px;background:#6576df;color:white;height:40px;padding:0 16px;font-weight:700}
    .create,.panel{background:var(--surface);border:1px solid var(--border);border-radius:14px;padding:18px;margin-bottom:14px}.create form{display:grid;grid-template-columns:2fr 1fr 1fr auto;gap:12px;align-items:end}.create label{display:grid;gap:7px;font-size:12px;font-weight:700}.create input,.create select{height:42px;border:1px solid var(--border);border-radius:8px;padding:0 10px}.create small{display:block;color:var(--muted);margin-top:12px}.form-error{color:#b74d5b}
    .tr{display:grid;grid-template-columns:1.2fr 1fr 1fr .8fr 1fr 1fr 70px;min-height:58px;align-items:center;border-bottom:1px solid var(--border);font-size:12px}.th{min-height:38px;color:var(--muted)}.tr i{font-style:normal;border-radius:20px;padding:5px 8px;background:#edf0f7}.tr i[data-status=CAPTURED],.tr i[data-status=SETTLED]{background:#daf4e8;color:#278261}.tr i[data-status=FAILED]{background:#ffe5e8;color:#b74d5b}.tr button{border:0;color:#6576df;background:transparent}.empty{text-align:center;padding:30px;color:var(--muted)}
    @media(max-width:800px){.tr{grid-template-columns:1fr 1fr 1fr}.tr>*:nth-child(2),.tr>*:nth-child(4),.tr>*:nth-child(6){display:none}.create form{grid-template-columns:1fr}}
  `]
})
export class PaymentsComponent implements OnInit {
  private api = inject(DomainService);
  private fb = inject(FormBuilder);
  payments = signal<PaymentResponse[]>([]);
  loaded = signal(false);
  initiating = signal(false);
  loading = false;
  error = '';

  form = this.fb.nonNullable.group({
    orderId: ['', Validators.required],
    method: ['UPI', Validators.required],
    token: [''],
    vpa: [''],
    wallet: [''],
    bankCode: ['']
  });
  get method() { return this.form.controls.method.value; }

  ngOnInit() {
    this.setMethodFields();
    this.load();
  }

  load() {
    this.api.getPayments().subscribe({
      next: response => { this.payments.set(response.data || []); this.loaded.set(true); },
      error: () => this.loaded.set(true)
    });
  }

  setMethodFields() {
    const controls = [this.form.controls.token, this.form.controls.vpa, this.form.controls.wallet, this.form.controls.bankCode];
    controls.forEach(control => control.clearValidators());
    if (this.method === 'CARD') this.form.controls.token.setValidators(Validators.required);
    if (this.method === 'UPI') this.form.controls.vpa.setValidators(Validators.required);
    if (this.method === 'WALLET') this.form.controls.wallet.setValidators(Validators.required);
    if (this.method === 'NETBANKING') this.form.controls.bankCode.setValidators(Validators.required);
    controls.forEach(control => control.updateValueAndValidity());
  }

  initiate() {
    if (this.form.invalid) return;
    this.loading = true;
    this.error = '';
    const value = this.form.getRawValue();
    const methodDetails: Record<string, string> = {};
    if (value.method === 'CARD') methodDetails['token'] = value.token;
    if (value.method === 'UPI') methodDetails['vpa'] = value.vpa;
    if (value.method === 'WALLET') methodDetails['wallet'] = value.wallet;
    if (value.method === 'NETBANKING') methodDetails['bankCode'] = value.bankCode;

    this.api.initiatePayment({ orderId: value.orderId, method: value.method, methodDetails }).subscribe({
      next: () => {
        this.loading = false;
        this.initiating.set(false);
        this.form.reset({ orderId: '', method: 'UPI', token: '', vpa: '', wallet: '', bankCode: '' });
        this.setMethodFields();
        this.load();
      },
      error: error => {
        this.loading = false;
        this.error = error.error?.message || 'Unable to initiate payment';
      }
    });
  }

  capture(payment: PaymentResponse) {
    this.api.capturePayment(payment.id).subscribe(() => this.load());
  }
}
