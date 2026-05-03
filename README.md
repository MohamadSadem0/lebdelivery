# Lebanon Local Commerce & Delivery Platform

Foundation monorepo for a Lebanon-focused local commerce, service, inventory, receipt, and delivery coordination platform.

## Systems Started

- Backend API foundation: `backend`
- Client mobile app foundation: `apps/client-mobile`
- Client web app foundation: `apps/client-web`
- Driver mobile app foundation: `apps/driver-mobile`
- Delivery web portal foundation: `apps/delivery-web`
- Business dashboard shell: `apps/business-dashboard`
- Admin dashboard shell: `apps/admin-dashboard`
- Shared TypeScript API-facing types: `packages/shared-types`

The platform uses one modular backend for the client, store, delivery, and service provider systems. Admin tooling is planned later.

## Prerequisites

- Java 21
- Maven 3.9+ or the Maven wrapper if added later
- Node.js 20+
- npm
- Docker Desktop

## Local Development

Create local env files from the examples:

```bash
cp .env.example .env
cp backend/.env.example backend/.env
cp apps/client-mobile/.env.example apps/client-mobile/.env
cp apps/client-web/.env.example apps/client-web/.env
cp apps/delivery-web/.env.example apps/delivery-web/.env
cp apps/driver-mobile/.env.example apps/driver-mobile/.env
cp apps/business-dashboard/.env.example apps/business-dashboard/.env
cp apps/admin-dashboard/.env.example apps/admin-dashboard/.env
```

On PowerShell:

```powershell
Copy-Item .env.example .env
Copy-Item backend/.env.example backend/.env
Copy-Item apps/client-mobile/.env.example apps/client-mobile/.env
Copy-Item apps/client-web/.env.example apps/client-web/.env
Copy-Item apps/delivery-web/.env.example apps/delivery-web/.env
Copy-Item apps/driver-mobile/.env.example apps/driver-mobile/.env
Copy-Item apps/business-dashboard/.env.example apps/business-dashboard/.env
Copy-Item apps/admin-dashboard/.env.example apps/admin-dashboard/.env
```

Start PostgreSQL and Redis:

```bash
docker compose up -d postgres redis
```

Run the backend:

```bash
cd backend
mvn spring-boot:run
```

The backend imports `backend/.env` when started from the `backend` directory.

If Java 21 or Maven is not installed locally, run the backend through Docker Compose:

```bash
docker compose --profile backend up -d backend
```

This starts PostgreSQL, Redis, and a Maven/JDK 21 backend container.

Check health:

```text
GET http://localhost:8080/actuator/health
```

OpenAPI documentation is available when the backend is running:

```text
GET http://localhost:8080/swagger-ui/index.html
GET http://localhost:8080/v3/api-docs
```

Run the client mobile app:

```bash
cd apps/client-mobile
npm install
npx expo start
```

Run the driver mobile app:

```bash
cd apps/driver-mobile
npm install
npx expo start
```

Run the client website:

```bash
cd apps/client-web
npm install
npm run dev
```

The client website runs on `http://localhost:5175` by default and talks to `VITE_API_BASE_URL`.

Run the delivery website:

```bash
cd apps/delivery-web
npm install
npm run dev
```

The delivery website runs on `http://localhost:5176` by default and talks to `VITE_API_BASE_URL`.

Run the business dashboard:

```bash
cd apps/business-dashboard
npm install
npm run dev
```

The dashboard runs on `http://localhost:5173` by default and talks to `VITE_API_BASE_URL`.

Run the admin dashboard:

```bash
cd apps/admin-dashboard
npm install
npm run dev
```

The admin dashboard runs on `http://localhost:5174` by default and requires an account with the `ADMIN` role.

For physical device testing, `localhost` points to the phone, not the computer. Set the mobile API URL to your computer LAN IP, for example:

```text
EXPO_PUBLIC_API_BASE_URL=http://192.168.1.50:8080/api/v1
```

## Current API Areas

- Authentication, roles, and entity ownership foundation
- Store onboarding and public store browsing
- Store type configuration and product form config
- Product and inventory MVP
- Client cart and order creation foundation
- Client web browsing, cart, checkout, saved addresses, orders, receipts, notifications, and support shell
- Store owner order management foundation
- Delivery request and driver job status MVP
- Delivery web portal shell for driver profile, available jobs, active delivery status, cash collection, notifications, and support
- Store owner business dashboard shell for stores, products, inventory, orders, deliveries, and receipts
- Provider owner business dashboard shell for providers, services, and service requests
- Admin dashboard shell for users, store approvals, driver approvals, receipts, and planned admin modules
- In-app notification APIs and notification screens across client, driver, business, and admin apps
- Support ticket MVP for client/driver issue creation and admin/support handling

## CI Checks

The GitHub Actions workflow runs:

- Backend tests with Java 21 and Maven.
- Shared TypeScript package build.
- Client mobile TypeScript check.
- Client web TypeScript check.
- Driver mobile TypeScript check.
- Delivery web TypeScript check.
- Business dashboard TypeScript check.
- Admin dashboard TypeScript check.

## Troubleshooting

- Port already in use: change `POSTGRES_PORT`, `REDIS_PORT`, or `SERVER_PORT` in the relevant env file.
- Database connection failed: confirm `docker compose ps`, then check `DATABASE_*` values in `backend/.env`.
- Backend cannot start locally: confirm `java -version` is Java 21 and `mvn -version` works, or use the Docker Compose backend profile.
- Physical phone cannot reach backend: replace `localhost` in mobile `.env` with your computer LAN IP.
- Flyway migration failure: inspect the first failed migration in `backend/src/main/resources/db/migration`; do not edit migrations already applied to a shared database.
- JWT secret too short: replace `JWT_ACCESS_SECRET` and `JWT_REFRESH_SECRET` with real 32+ character secrets outside development.
- CORS issue: add the frontend origin to `CORS_ALLOWED_ORIGINS` in `backend/.env`.
- Business dashboard cannot login: confirm `VITE_API_BASE_URL` points to the backend `/api/v1` base URL and the user has a business role.
- Admin dashboard cannot login: confirm the user has an `ADMIN` role and `VITE_API_BASE_URL` points to the backend `/api/v1` base URL.
- Client website cannot checkout: confirm the user has a `CLIENT` role and `VITE_API_BASE_URL` points to the backend `/api/v1` base URL.
- Delivery website cannot show jobs: confirm the user has `INDEPENDENT_DRIVER` or `STORE_DRIVER`, the driver profile exists, and `VITE_API_BASE_URL` points to the backend `/api/v1` base URL.

## MVP Boundaries

Do not add microservices, Kubernetes, payment gateways, wallets, full chat, push notifications, WebSockets, AI features, full receipts, or delivery matching yet. Polling is acceptable for MVP tracking, and Redis should later hold live driver location instead of writing every GPS update to PostgreSQL.
