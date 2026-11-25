import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { ApiResponse, ApiKeyCreateResponse, ApiKeyResponse, OrderResponse, PaymentResponse, TransitionLog, TokenizeResponse } from '../models/api.models';

@Injectable({ providedIn: 'root' })
export class DomainService {
  private http = inject(HttpClient);
  private base = environment.apiBaseUrl;

  createOrder(payload: { money: { amountUnits: number; currency: string }; receipt?: string; notes?: Record<string, unknown>; expireAt?: string }) {
    return this.http.post<ApiResponse<OrderResponse>>(`${this.base}/api/v1/order`, payload);
  }
  getOrders() { return this.http.get<ApiResponse<OrderResponse[]>>(`${this.base}/api/v1/order/merchant`); }
  getOrder(id: string) { return this.http.get<ApiResponse<OrderResponse>>(`${this.base}/api/v1/order/${id}`); }
  cancelOrder(id: string) { return this.http.delete<ApiResponse<OrderResponse>>(`${this.base}/api/v1/order/${id}`); }
  getOrderPayments(id: string) { return this.http.get<ApiResponse<PaymentResponse[]>>(`${this.base}/api/v1/order/${id}/payments`); }

  initiatePayment(payload: { orderId: string; method: string; methodDetails?: Record<string, unknown> }) {
    return this.http.post<ApiResponse<PaymentResponse>>(`${this.base}/api/v1/payments`, payload);
  }
  capturePayment(id: string) { return this.http.post<ApiResponse<PaymentResponse>>(`${this.base}/api/v1/payments/${id}/capture`, {}); }
  getPayments() { return this.http.get<ApiResponse<PaymentResponse[]>>(`${this.base}/api/v1/payments/all-payment`); }

  getTransitionLogs() { return this.http.get<ApiResponse<TransitionLog[]>>(`${this.base}/api/v1/payment-transition-logs`); }
  getPaymentLogs(id: string) { return this.http.get<ApiResponse<TransitionLog[]>>(`${this.base}/api/v1/payment-transition-logs/payment/${id}`); }

  createApiKey(merchantId: string, environmentName: string) {
    return this.http.post<ApiResponse<ApiKeyCreateResponse>>(`${this.base}/api/v1/merchants/${merchantId}/apikey`, { environment: environmentName });
  }
  getApiKeys(merchantId: string) { return this.http.get<ApiResponse<ApiKeyResponse[]>>(`${this.base}/api/v1/merchants/${merchantId}/apikey`); }
  revokeApiKey(merchantId: string, keyId: string) { return this.http.delete<void>(`${this.base}/api/v1/merchants/${merchantId}/apikey/${keyId}`); }
  rotateApiKey(merchantId: string, keyId: string) {
    return this.http.post<ApiResponse<ApiKeyCreateResponse>>(`${this.base}/api/v1/merchants/${merchantId}/apikey/${keyId}/rotate`, {});
  }
  tokenize(payload: { pan: string; cvv: string; expiryMonth: number; expiryYear: number; customerId: string; cardHolderName: string }) {
    return this.http.post<ApiResponse<TokenizeResponse>>(`${this.base}/api/v1/vault/tokenize`, payload);
  }
}
