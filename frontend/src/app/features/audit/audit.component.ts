import { Component, inject, OnInit, signal } from '@angular/core';
import { DatePipe, NgFor, NgIf } from '@angular/common';
import { DomainService } from '../../core/services/domain.service';
import { TransitionLog } from '../../core/models/api.models';
@Component({standalone:true,imports:[DatePipe,NgFor,NgIf],template:`<div class="heading"><p>Operations</p><h1>Payment audit logs</h1></div><section class="panel"><div class="tr th"><span>Transaction</span><span>Payment</span><span>Transition</span><span>Event</span><span>Actor</span><span>Occurred</span></div><div class="tr" *ngFor="let l of logs()"><span><b>{{l.id.slice(0,8)}}…</b></span><span>{{l.paymentId.slice(0,8)}}…</span><span>{{l.fromStatus||'—'}} → {{l.toStatus}}</span><span>{{l.event}}</span><span>{{l.actor||'SYSTEM'}}</span><span>{{l.occurredAt|date:'MMM d, HH:mm'}}</span></div><div class="empty" *ngIf="loaded()&&!logs().length">No transition logs found.</div></section>`,styles:[`.heading p{color:var(--muted);margin:0}.heading h1{margin:4px 0 20px}.panel{background:var(--surface);border:1px solid var(--border);border-radius:14px;padding:18px}.tr{display:grid;grid-template-columns:1fr 1fr 1.4fr 1.2fr 1fr 1fr;min-height:58px;align-items:center;border-bottom:1px solid var(--border);font-size:12px}.th{min-height:38px;color:var(--muted)}.empty{text-align:center;padding:30px;color:var(--muted)}@media(max-width:800px){.tr{grid-template-columns:1fr 1fr 1.4fr}.tr>*:nth-child(4),.tr>*:nth-child(5),.tr>*:nth-child(6){display:none}}`]})
export class AuditComponent implements OnInit {
 private api=inject(DomainService);logs=signal<TransitionLog[]>([]);loaded=signal(false);
 ngOnInit(){this.api.getTransitionLogs().subscribe({next:r=>{this.logs.set(r.data||[]);this.loaded.set(true)},error:()=>this.loaded.set(true)});}
}
