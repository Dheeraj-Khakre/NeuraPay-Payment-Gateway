export interface ApiResponse<T> { success: boolean; message: string; data: T; timestamp: string }
export interface Money { amountUnits: number; currency: string }
export type OrderStatus = 'CREATED'|'ATTEMPTED'|'PAID'|'COMPLETED'|'FAILED'|'CANCELLED';
export type PaymentStatus = 'CREATED'|'INITIATED'|'AUTHORIZING'|'AUTHORIZED'|'CAPTURING'|'CAPTURED'|'FAILED'|'REFUNDING'|'SETTLED'|'REFUNDING'|'REFUNDED'|'PARTIALLY_REFUNDED'|'AUTHORIZATION_EXPIRED'|'PENDING'|'CANCELLED';
export type PaymentMethod = 'CARD'|'UPI'|'WALLET'|'NETBANKING';
export interface OrderResponse { id: string; merchantId: string; status: OrderStatus; amount: Money; attempts: number; receipt?: string; notes?: Record<string, unknown>; expireAt?: string; createAt: string }
export interface PaymentResponse { id: string; orderId: string; merchantId: string; idempotencyKey: string; amount: Money; status: PaymentStatus; method: PaymentMethod; methodDetails?: Record<string, unknown>; bankReference?: string; errorCode?: string; errorDescription?: string; refundedAmountPaise: number; authorizedAt?: string; failedAt?: string; capturedAt?: string; settledAt?: string; createdAt: string; updatedAt: string }
export interface AuthResponse { accessToken: string; tokenType: string; expiresInSeconds: number; userId: string; merchantId: string; email: string; role: string }
export interface MerchantSignUpRequest { businessName: string; businessType: string; name: string; email: string; password: string; phone: string }
export interface MerchantResponse { id: string; merchantCode: string; businessName: string; businessType: string; legalName: string; email: string; phone: string; websiteUrl?: string; gstId?: string; pan?: string; status: string; kycVerified: boolean }
export interface ApiKeyCreateResponse { id: string; keyId: string; keySecretHash: string; environment: string }
export interface ApiKeyResponse { id: string; keyId: string; environment: string; enable: boolean; createAt: string; lastUsedAt?: string }
export interface TransitionLog { id: string; paymentId: string; fromStatus?: PaymentStatus; toStatus: PaymentStatus; event: string; actor?: string; reason?: string; occurredAt: string }
export interface TokenizeResponse { token: string; lastFour: string; brand: string; expiryMonth: number; expiryYear: number }
