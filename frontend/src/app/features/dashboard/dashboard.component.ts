import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { DatePipe, DecimalPipe, NgFor, NgIf } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DomainService } from '../../core/services/domain.service';
import { OrderResponse, PaymentResponse } from '../../core/models/api.models';
import { MoneyAmountPipe } from '../../shared/pipes/money-amount.pipe';

@Component({
  standalone: true,
  imports: [DatePipe, DecimalPipe, NgFor, NgIf, RouterLink, MoneyAmountPipe],
  template: `
    <div class="heading">
      <div><p>{{ today | date:'EEEE, d MMMM' }}</p><h1>Good morning</h1></div>
      <span class="range">All available data</span>
    </div>
    <div class="kpis">
      <article><p>Captured volume · INR</p><h2>{{ gross() | number:'1.2-2' }} <small>INR</small></h2><span class="trend">From captured and settled payments</span></article>
      <article><p>Successful payments</p><h2>{{ successCount() }}</h2><span>of {{ payments().length }} transactions</span></article>
      <article><p>Failed payments</p><h2>{{ failedCount() }}</h2><span>{{ successRate() | number:'1.0-1' }}% authorized/captured/settled</span></article>
      <article><p>Orders</p><h2>{{ orders().length }}</h2><span>{{ paidOrders() }} paid</span></article>
    </div>
    <div class="grid-main">
      <article class="chart-card">
        <div class="card-head"><div><p>Transaction trend</p><h3>Payment activity · last 7 days</h3></div><div class="legend"><i></i>Payments</div></div>
        <div class="chart-bars" *ngIf="hasTrend(); else noTrend">
          <div class="bar-item" *ngFor="let point of trend()">
            <span class="bar-count">{{ point.count }}</span>
            <div class="bar-track"><i [style.height.%]="point.height"></i></div>
            <small>{{ point.label }}</small>
          </div>
        </div>
        <ng-template #noTrend><div class="chart-empty">No transaction activity in the last 7 days.</div></ng-template>
      </article>
      <article class="method-card">
        <div class="card-head"><div><p>Payment methods</p><h3>Distribution</h3></div></div>
        <div class="donut" [style.background]="methodDonut()"><div><b>{{ payments().length }}</b><small>payments</small></div></div>
        <div class="methods"><span *ngFor="let method of methodRows()"><i [style.background]="method.color"></i>{{ method.name }}<b>{{ method.count }}</b></span><span *ngIf="!payments().length" class="muted">No payment data yet</span></div>
      </article>
    </div>
    <article class="transactions">
      <div class="card-head"><div><p>Latest activity</p><h3>Recent transactions</h3></div><a routerLink="/payments">View all →</a></div>
      <div class="table">
        <div class="tr th"><span>Payment ID</span><span>Order</span><span>Amount</span><span>Method</span><span>Status</span><span>Date</span></div>
        <div class="tr" *ngFor="let payment of payments().slice(0,7)">
          <span><b>{{ payment.id.slice(0,8) }}</b></span><span>{{ payment.orderId.slice(0,8) }}</span>
          <span>{{ payment.amount | moneyAmount | number:'1.2-2' }} {{ payment.amount.currency }}</span><span>{{ payment.method }}</span>
          <span><i class="status" [attr.data-status]="payment.status">{{ payment.status }}</i></span><span>{{ payment.createdAt | date:'MMM d, HH:mm' }}</span>
        </div>
        <div class="empty" *ngIf="!payments().length">No payment data has been recorded yet.</div>
      </div>
    </article>`,
  styles: [`
    :host{display:block}.heading{display:flex;justify-content:space-between;align-items:center;margin-bottom:22px}.heading p,.card-head p{color:var(--muted);margin:0 0 4px}.heading h1{margin:0;font-size:28px}.range{border:1px solid var(--border);border-radius:9px;padding:11px 14px;color:var(--muted);font-size:12px}
    .kpis{display:grid;grid-template-columns:repeat(4,1fr);gap:14px}.kpis article,.chart-card,.method-card,.transactions{background:var(--surface);border:1px solid var(--border);border-radius:14px;box-shadow:0 8px 30px rgba(31,38,61,.035)}.kpis article{padding:20px}.kpis p{color:var(--muted);margin:0 0 10px;font-size:13px}.kpis h2{margin:0 0 12px;font-size:25px}.kpis h2 small{font-size:12px;color:var(--muted)}.kpis article>span{font-size:12px;color:var(--muted)}.trend{color:#39a77a}
    .grid-main{display:grid;grid-template-columns:2fr 1fr;gap:14px;margin-top:14px}.chart-card,.method-card,.transactions{padding:20px}.card-head{display:flex;justify-content:space-between;align-items:center}.card-head h3{margin:0;font-size:16px}.legend{font-size:12px;color:var(--muted)}.legend i{display:inline-block;width:8px;height:8px;border-radius:50%;background:#6679e8;margin-right:6px}
    .chart-bars{height:225px;margin-top:16px;display:grid;grid-template-columns:repeat(7,1fr);gap:12px;align-items:end}.bar-item{height:100%;display:flex;flex-direction:column;align-items:center;justify-content:flex-end;gap:7px}.bar-count,.bar-item small{font-size:10px;color:var(--muted)}.bar-track{width:min(34px,70%);height:170px;display:flex;align-items:flex-end;border-bottom:1px solid var(--border)}.bar-track i{display:block;width:100%;min-height:0;border-radius:6px 6px 0 0;background:linear-gradient(180deg,#8292f1,#596fd9);transition:height .25s}.chart-empty{height:225px;display:grid;place-items:center;color:var(--muted);font-size:13px}
    .donut{width:150px;height:150px;border-radius:50%;margin:18px auto;display:grid;place-items:center;position:relative}.donut:before{content:'';position:absolute;width:108px;height:108px;border-radius:50%;background:var(--surface)}.donut div{z-index:1;text-align:center}.donut b,.donut small{display:block}.methods{display:grid;gap:8px}.methods span{display:flex;align-items:center;font-size:12px}.methods i{width:8px;height:8px;border-radius:50%;margin-right:8px}.methods b{margin-left:auto}.muted{color:var(--muted)}
    .transactions{margin-top:14px}.transactions a{color:#6175da;text-decoration:none}.table{margin-top:14px}.tr{display:grid;grid-template-columns:1.2fr 1fr 1fr .8fr 1fr 1fr;align-items:center;min-height:58px;border-bottom:1px solid var(--border);font-size:12px}.th{min-height:38px;color:var(--muted);font-size:11px}.status{border-radius:20px;padding:5px 8px;font-style:normal;background:#edf0f7}.status[data-status=CAPTURED],.status[data-status=SETTLED]{background:#daf4e8;color:#278261}.status[data-status=FAILED]{background:#ffe5e8;color:#b74d5b}.empty{text-align:center;padding:30px;color:var(--muted)}
    @media(max-width:1100px){.kpis{grid-template-columns:repeat(2,1fr)}.grid-main{grid-template-columns:1fr}.method-card{display:grid;grid-template-columns:1fr 180px 1fr}.method-card .card-head{grid-column:1}.donut{grid-column:2}.methods{grid-column:3}.tr{grid-template-columns:1fr 1fr 1fr .8fr .9fr}.tr>*:last-child{display:none}}
    @media(max-width:700px){.kpis{grid-template-columns:1fr 1fr}.grid-main{display:block}.method-card{display:block;margin-top:14px}.tr{grid-template-columns:1fr 1fr 1fr}.tr>*:nth-child(2),.tr>*:nth-child(4),.tr>*:nth-child(6){display:none}}
  `]
})
export class DashboardComponent implements OnInit {
  private api = inject(DomainService);
  readonly today = new Date();
  orders = signal<OrderResponse[]>([]);
  payments = signal<PaymentResponse[]>([]);

  gross = computed(() => this.payments()
    .filter(p => p.amount.currency === 'INR' && ['CAPTURED', 'SETTLED', 'REFUNDED', 'PARTIALLY_REFUNDED'].includes(p.status))
    .reduce((sum, p) => sum + p.amount.amountUnits, 0) / 100);
  successCount = computed(() => this.payments().filter(p => ['CAPTURED', 'SETTLED', 'AUTHORIZED'].includes(p.status)).length);
  failedCount = computed(() => this.payments().filter(p => p.status === 'FAILED').length);
  successRate = computed(() => this.payments().length ? this.successCount() / this.payments().length * 100 : 0);
  paidOrders = computed(() => this.orders().filter(o => o.status === 'PAID' || o.status === 'COMPLETED').length);

  methodRows = computed(() => {
    const counts = new Map<string, number>();
    this.payments().forEach(payment => counts.set(payment.method, (counts.get(payment.method) || 0) + 1));
    const colors = ['#6679e8', '#8bd6be', '#f2b66d', '#d2a4e8'];
    return [...counts].map(([name, count], index) => ({ name, count, color: colors[index % colors.length] }));
  });
  methodDonut = computed(() => {
    const rows = this.methodRows();
    const total = this.payments().length;
    if (!total) return 'conic-gradient(#dfe3ef 0 100%)';
    let end = 0;
    const segments = rows.map(row => {
      const start = end;
      end += row.count / total * 100;
      return `${row.color} ${start}% ${end}%`;
    });
    return `conic-gradient(${segments.join(',')})`;
  });
  trend = computed(() => {
    const today = new Date();
    const days = Array.from({ length: 7 }, (_, index) => {
      const date = new Date(today);
      date.setDate(today.getDate() - 6 + index);
      return date;
    });
    const counts = days.map(day => this.payments().filter(payment => {
      const date = new Date(payment.createdAt);
      return date.getFullYear() === day.getFullYear() && date.getMonth() === day.getMonth() && date.getDate() === day.getDate();
    }).length);
    const max = Math.max(...counts, 1);
    return days.map((date, index) => ({
      label: date.toLocaleDateString(undefined, { weekday: 'short' }),
      count: counts[index],
      height: counts[index] ? counts[index] / max * 100 : 0
    }));
  });

  hasTrend = computed(() => this.trend().some(point => point.count > 0));

  ngOnInit() {
    this.api.getOrders().subscribe(response => this.orders.set(response.data || []));
    this.api.getPayments().subscribe(response => this.payments.set(response.data || []));
  }
}
