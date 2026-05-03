# Lebanon Local Commerce & Delivery Platform

## Project Overview

This monorepo contains the foundation for a Lebanon-focused local commerce, services, inventory, receipts, and delivery coordination platform.

The platform is organized around four main product systems:

- Store System
- Delivery System
- Service Provider System
- Client System

An Admin System will be added later for approvals, monitoring, support, payments, receipts, issues, and quality control.

The platform starts with three implementation foundations:

- `backend`: Spring Boot modular monolith API.
- `apps/client-mobile`: Expo React Native client app.
- `apps/driver-mobile`: Expo React Native driver app.

Future systems will include store, service provider, and admin dashboards, but they should be added only when the backend and mobile foundations are stable.

## Chosen Stack

- Backend: Java 21, Spring Boot, Spring Security, Spring Data JPA, PostgreSQL, Flyway, Bean Validation, Actuator.
- Mobile: React Native, Expo, TypeScript, React Navigation.
- Client state and server data: Zustand and TanStack Query.
- Forms and validation: React Hook Form and Zod.
- Secure storage: Expo Secure Store.
- Location later: Expo Location and React Native Maps.
- Push notifications later: Firebase Cloud Messaging.
- Cache and live location later: Redis.
- Storage later: S3-compatible object storage.

## Folder Structure

```text
backend/
  src/main/java/com/lebanonplatform/
    common/
    modules/
  src/main/resources/
    db/migration/

apps/
  client-mobile/
  driver-mobile/

packages/
  shared-types/

docs/
```

## Coding Conventions

- Keep the backend a modular monolith.
- Keep module-specific code inside `com.lebanonplatform.modules.<module>`.
- Keep shared cross-cutting backend code inside `com.lebanonplatform.common`.
- Do not expose JPA entities directly through controllers.
- Use request and response DTOs for every API boundary.
- Use UUID primary keys.
- Use `BigDecimal` for money.
- Store order item name and price snapshots.
- Store receipt snapshots.
- Prefer simple, explicit code over speculative abstractions.
- Add business logic only when a real workflow needs it.

## Security Rules

- Never hardcode secrets.
- Never commit real `.env` files.
- Never expose password hashes through APIs.
- Do not store tokens in plain AsyncStorage.
- Use Expo Secure Store for mobile tokens.
- The backend is the source of truth for prices, totals, delivery fees, and permissions.
- Frontend permission checks are UX only.
- Every future endpoint must check role and entity ownership.
- Entity-level permissions are required for stores, drivers, service providers, deliveries, orders, payments, receipts, and support tickets.
- Protected endpoints must eventually check authentication, role, entity access, and whether the resource is in the correct state for the requested action.

## Privacy Rules

- Collect only the data needed for the workflow.
- Treat phone numbers, addresses, delivery locations, payment status, receipts, and support tickets as sensitive.
- Do not expose internal audit, permission, password, token, or operational fields in API responses.
- Store future live location data with tight retention and use Redis for latest live position.

## Store-Type Architecture

Use one Core Store System, not separate systems per store type.

- Store types are represented by configuration templates.
- Important searchable fields stay as real columns.
- Flexible product/store-type fields use attributes or JSON config only where the data varies by store type.
- Inventory supports quantity, variant, availability, and weight modes.
- The backend remains the source of truth for product availability, totals, inventory, and permissions.

## Testing Rules

- Backend tests should use JUnit 5 and Spring Boot test support.
- Add integration tests for repositories, security-sensitive endpoints, and money/status transitions once those workflows exist.
- Mobile tests should focus first on hooks, stores, validation schemas, and important screen behavior.
- Do not mock away authorization or pricing logic in backend tests.

## MVP Boundaries

Do not overbuild the MVP.

- Do not add microservices.
- Do not add Kubernetes.
- Do not add a payment gateway yet.
- Do not add full chat yet.
- Do not add AI features.
- Do not overbuild realtime tracking; polling is acceptable for MVP.
- Do not write every future driver location update directly to PostgreSQL; use Redis for latest live location.
- Use pagination for future list APIs.
