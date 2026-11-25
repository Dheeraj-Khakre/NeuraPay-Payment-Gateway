# NeuraPay Backend API Mapping

The backend uses Spring Boot under `/NeuraPay`, is multi-tenant, and requires a JWT Bearer token for every endpoint except login and signup. All protected endpoints derive merchant identity from the authenticated JWT principal.

## Common contracts

- Spring Boot base URL: `http://localhost:8080`; Angular development requests use the same-origin `/api` proxy configured in `proxy.conf.json`.
- Auth header: `Authorization: Bearer <accessToken>`
- Successful wrapper: `{ "success": true, "message": "string", "data": T, "timestamp": "instant" }`
- Validation wrapper: `{ "success": false, "message": "Validation failed", "data": { "<field>": "<message>" }, "timestamp": "instant" }`
- Resource errors: `{ "errorCore": "string", "errorDescription": "string", "timestamp": "instant", "fieldErrors": null }`
- The backend currently declares no CORS configuration. Do not call it cross-origin from the browser; `npm start` proxies `/api/**` to the backend without backend changes.

## Endpoint matrix

| Endpoint | Method | Purpose | Headers | Request payload | Response data | Auth | Angular service/UI |
|---|---|---|---|---|---|---|---|
| `/api/v1/auth/signup` | POST | Create merchant | `Content-Type: application/json` | `{businessName,businessType,name,email,password,phone}` | `MerchantResponse` | Public | `AuthService`, Sign up |
| `/api/v1/auth/login` | POST | Authenticate merchant user | `Content-Type: application/json` | `{email,password}` | `AuthResponse` (`accessToken`, `tokenType`, `expiresInSeconds`, IDs, email, role) | Public | `AuthService`, Login |
| `/api/v1/order` | POST | Create order | Bearer | `{money:{amountUnits,currency},receipt?,notes?,expireAt?}` | `OrderResponse` | JWT | `DomainService`, Orders |
| `/api/v1/order/{orderId}` | GET | Get one order | Bearer | — | `OrderResponse` | JWT, merchant-scoped | `DomainService`, Orders detail-ready |
| `/api/v1/order/{orderId}` | DELETE | Cancel order | Bearer | — | `OrderResponse` | JWT, merchant-scoped | `DomainService`, Orders |
| `/api/v1/order/{orderId}/payments` | GET | List payments for order | Bearer | — | `PaymentResponse[]` (controller declares generic data) | JWT | `DomainService`, payment drill-down |
| `/api/v1/order/merchant` | GET | List merchant orders | Bearer | — | `OrderResponse[]` | JWT | `DomainService`, Orders and Dashboard |
| `/api/v1/payments` | POST | Initiate payment | Bearer | `{orderId,method,methodDetails?}` | `PaymentResponse` | JWT | `DomainService`, Payments |
| `/api/v1/payments/{paymentId}/capture` | POST | Capture payment | Bearer | `{}` | `PaymentResponse` | JWT | `DomainService`, Payments |
| `/api/v1/payments/all-payment` | GET | List merchant payments | Bearer | — | `PaymentResponse[]` | JWT | `DomainService`, Payments and Dashboard |
| `/api/v1/payment-transition-logs` | GET | List transition logs | Bearer | — | `PaymentTransitionLogDto[]` | JWT | `DomainService`, Audit |
| `/api/v1/payment-transition-logs/payment/{paymentId}` | GET | List logs for a payment | Bearer | — | `PaymentTransitionLogDto[]` | JWT | `DomainService`, payment detail-ready |
| `/api/v1/payment-transition-logs/{transactionId}` | GET | Get transition log | Bearer | — | `PaymentTransitionLogDto` | JWT | `DomainService`, audit detail-ready |
| `/api/v1/merchants/{merchantId}/apikey` | POST | Create API key | Bearer | `{environment}` | `ApiKeyCreateResponse` | JWT, matching merchant | `DomainService`, API keys |
| `/api/v1/merchants/{merchantId}/apikey` | GET | List API keys | Bearer | — | `ApiKeyResponse[]` | JWT, matching merchant | `DomainService`, API keys |
| `/api/v1/merchants/{merchantId}/apikey/{keyId}` | DELETE | Revoke API key | Bearer | — | No content | JWT, matching merchant | `DomainService`, API keys |
| `/api/v1/merchants/{merchantId}/apikey/{keyId}/rotate` | POST | Rotate API key | Bearer | `{}` | `ApiKeyCreateResponse` | JWT, matching merchant | `DomainService`, API keys |
| `/api/v1/vault/tokenize` | POST | Tokenize card | Bearer | `{pan,cvv,expiryMonth,expiryYear,customerId,cardHolderName}` | `TokenizeResponse` | JWT | `DomainService`, Card vault |

## Enumerations

- `BusinessType`: LLP, PROPRIETORSHIP, PRIVATE_LIMITED, PUBLIC_LIMITED, SOLE_PROPRIETORSHIP, PARTNERSHIP, HUF, TRUST, NGO
- `Environment`: SANDBOX, PRODUCTION, LIVE, STAGING, TEST
- `OrderStatus`: CREATED, ATTEMPTED, PAID, COMPLETED, FAILED, CANCELLED
- `PaymentMethod`: CARD, UPI, WALLET, NETBANKING
- `PaymentStatus`: CREATED, INITIATED, AUTHORIZING, AUTHORIZED, CAPTURING, CAPTURED, FAILED, REFUNDING, SETTLED, REFUNDING, REFUNDED, PARTIALLY_REFUNDED, AUTHORIZATION_EXPIRED, PENDING, CANCELLED
- `UserRoles`: OWNER, TEAM, ADMIN, MANAGER, FINANCE, OPERATIONS, SUPPORT, DEVELOPER, AUDITOR
- `PaymentEvent`: AUTHORIZE_ATTEMPT, AUTHORIZE_SUCCESS, AUTHORIZE_FAILURE, CAPTURE_REQUEST, CAPTURE_SUCCESS, CAPTURE_FAILURE, REFUND_INITIATED, REFUND_SUCCESS, REFUND_FAILURE, PARTIAL_REFUND_INITIATED, PARTIAL_REFUND_SUCCESS, PARTIAL_REFUND_FAILURE, CANCEL_REQUEST, CANCEL_SUCCESS, SETTLE, CAPTURE_EXPIRED, PAYMENT_FAILED

## Validation highlights

Merchant signup: business name 2-150 characters, legal name max 150, valid email max 150, password 8-100, phone 10-20.
Order: amount `Money` is required; receipt max 100.
Payment: order ID and payment method are required.
Payment method details used by the backend processors: CARD requires a vault `token`; UPI requires `vpa`; NETBANKING requires `bankCode`; WALLET requires `wallet`. The Angular payment form exposes the corresponding non-card sensitive token/details and does not collect card PAN/CVV there.
Card vault: PAN passes Luhn validation; CVV 3-4 digits; expiry month 1-12; year 2000-9999 and not past; customer ID required; cardholder name 3+ characters.
The backend `MoneyDto` validation describes INR amounts in paise. The dashboard converts INR `amountUnits` to rupees for display; creation forms accept amount units as integer minor units.

## Integration matrix

| UI feature | Backend endpoint | Status |
|---|---|---|
| Login/signup | Auth APIs | Implemented |
| Overview metrics | Orders and all payments | Implemented using returned data; no server aggregation endpoint |
| Transaction trend and method distribution | All payments | Implemented client-side from actual returned records; no historical/report endpoint |
| Order create/list/cancel/details | Order APIs | Implemented |
| Payment initiate/list/capture | Payment APIs | Implemented |
| Payment transition audit | Transition log APIs | Implemented |
| API key create/list/rotate/revoke | API key APIs | Implemented |
| Card tokenization | Vault API | Implemented |

## Unsupported / missing APIs

No REST controller exists for customer CRUD; refunds; settlements; webhooks/DLQ; subscriptions/payment links; merchant profile update; reports or server-side dashboard analytics; pagination, sorting, filtering, or order search. Their entities or DTOs alone do not constitute callable APIs. They are not displayed as operational features.
