# NeuraPay Angular Frontend

Angular 20 standalone SPA for the existing Spring Boot backend.

## Local run

Requirements: Node.js 20+.

```powershell
cd frontend
npm install
npm start
```

Open `http://localhost:4200`. The Angular development server proxies `/api` to `http://localhost:8080` so browser requests are same-origin.

Start the backend separately:

```powershell
cd NeuraPay
.\mvnw.cmd spring-boot:run
```

The backend has no CORS configuration. Keep the dev proxy enabled; do not make browser calls directly to the backend or change backend sources.

## Docker

```powershell
cd frontend
docker build -t neurapay/frontend:local .
docker run --rm -p 8080:80 neurapay/frontend:local
```

Open `http://localhost:8080`. Nginx proxies `/api/*` to `host.docker.internal:8080` for Docker Desktop.

## Implemented areas

JWT login/signup, overview metrics from real order/payment data, orders, payments and capture, API keys, card vault tokenization, payment transition logs, and settings limitation information.

API details and unsupported features are documented in `docs/BACKEND_API_MAPPING.md`.
