# 03 — Technical Architecture, Best Practices, Folder Structure, Models, Security, Privacy, Performance & Stack

## Purpose

This file defines the recommended technical foundation for the Lebanon Local Commerce & Delivery Platform.

Chosen stack:

```text
Backend: Spring Boot
Web Dashboards: React
Mobile Apps: React Native
Database: PostgreSQL
Cache / Realtime Support: Redis
Push Notifications: Firebase Cloud Messaging
Maps: Google Maps or Mapbox
Storage: S3-compatible object storage
```

The platform includes:

```text
Client Mobile App
Driver Mobile App
Store / Service Provider Dashboard
Admin Dashboard
Spring Boot Backend API
PostgreSQL Database
Redis
Object Storage
Push Notification System
Maps Provider
```

---

## 1. Architecture Principle

Start as a **modular monolith**, not microservices.

### Why Modular Monolith First

For MVP and early development, a modular monolith is better because:

- Faster to build.
- Easier to debug.
- Easier to deploy.
- Easier to maintain with a small team.
- Avoids unnecessary distributed system complexity.
- Keeps business logic in one place.
- Still allows clean separation by modules.

### Future Microservice Candidates

Only after real scaling pressure, modules can be separated:

- Delivery matching service.
- Notification service.
- Payment service.
- Search service.
- Analytics service.

Do not start with microservices.

---

## 2. High-Level Architecture

```text
Client Mobile App
Driver Mobile App
Business Dashboard
Admin Dashboard
        ↓
Spring Boot REST API
        ↓
Application Modules
        ↓
Domain Services
        ↓
Repositories
        ↓
PostgreSQL

Supporting:
- Redis
- Firebase Cloud Messaging
- Object Storage
- Maps Provider
- Monitoring / Logs
```

---

## 3. Repository Strategy

Use a **monorepo** at the beginning.

```text
lebanon-platform/
  backend/
  apps/
    client-mobile/
    driver-mobile/
    business-dashboard/
    admin-dashboard/
  packages/
    shared-types/
    shared-ui/
    shared-utils/
  docs/
  infra/
```

### Why Monorepo

- Easier to manage all apps together.
- Shared API types can be reused.
- Easier to keep frontend and backend aligned.
- Easier for one developer or small team.
- Easier to create consistent UI components.
- Easier to update contracts and models.

### When to Split Repositories

Split only when:

- Team becomes larger.
- Different teams own different systems.
- Deployment becomes complex.
- Security boundaries require separation.

---

## 4. Full Recommended Project Structure

```text
lebanon-platform/
  README.md
  .gitignore
  .editorconfig
  .env.example
  docker-compose.yml

  docs/
    product/
      proposal.md
      functionalities-workflows-api-stack.md
      pages-models-flow-design.md
    architecture/
      best-practices-architecture-stack.md
      api-guidelines.md
      database-design.md
      security-guidelines.md
    decisions/
      ADR-0001-architecture.md
      ADR-0002-auth-strategy.md

  backend/
    pom.xml
    Dockerfile
    src/
      main/
        java/
          com/company/platform/
        resources/
          application.yml
          application-dev.yml
          application-prod.yml
          db/migration/
      test/
        java/
          com/company/platform/

  apps/
    client-mobile/
    driver-mobile/
    business-dashboard/
    admin-dashboard/

  packages/
    shared-types/
    shared-ui/
    shared-utils/

  infra/
    docker/
    nginx/
    scripts/
    monitoring/
```

---

## 5. Backend Stack

### Main Technologies

```text
Java 21+
Spring Boot
Spring Web
Spring Security
Spring Data JPA
PostgreSQL
Flyway
Redis
Bean Validation
MapStruct
Springdoc OpenAPI
JUnit 5
Testcontainers
Docker
```

### Recommended Libraries

Core:

```text
spring-boot-starter-web
spring-boot-starter-security
spring-boot-starter-validation
spring-boot-starter-data-jpa
spring-boot-starter-actuator
spring-boot-starter-websocket
```

Database:

```text
PostgreSQL Driver
Flyway
Hibernate
```

Mapping:

```text
MapStruct
```

API Docs:

```text
springdoc-openapi
Swagger UI
```

Security / JWT:

```text
Spring Security
OAuth2 Resource Server
Nimbus JOSE JWT
```

Testing:

```text
JUnit 5
Mockito
AssertJ
Spring Boot Test
Testcontainers
MockMvc
WireMock
```

Performance / Reliability:

```text
Redis
Caffeine Cache
Bucket4j
Resilience4j
Micrometer
OpenTelemetry
```

Storage:

```text
AWS SDK S3
Cloudflare R2 compatible S3 API
```

Notifications:

```text
Firebase Admin SDK
```

---

## 6. Backend Version Strategy

Use stable versions, not random latest versions.

Recommended approach:

```text
Java: latest LTS supported by chosen Spring Boot version
Spring Boot: latest stable compatible version
PostgreSQL: stable major version supported by hosting provider
Redis: stable managed version
```

Do not upgrade major versions in production without:

- Reading migration guide.
- Checking dependency compatibility.
- Running full automated tests.
- Testing staging.
- Backing up database.

---

## 7. Backend Package Structure

```text
backend/src/main/java/com/company/platform/
  PlatformApplication.java

  common/
    config/
    constants/
    exceptions/
    security/
    utils/
    validation/
    pagination/
    audit/
    mapper/

  modules/
    auth/
    users/
    roles/
    clients/
    stores/
    products/
    inventory/
    orders/
    serviceproviders/
    services/
    servicerequests/
    drivers/
    deliveries/
    payments/
    receipts/
    notifications/
    messaging/
    ratings/
    support/
    admin/
    files/
    reports/
```

### Example Module Structure

```text
modules/orders/
  api/
    OrderController.java
    StoreOrderController.java
    ClientOrderController.java
    AdminOrderController.java
  application/
    OrderService.java
    OrderStatusService.java
    OrderPricingService.java
    OrderTimelineService.java
  domain/
    Order.java
    OrderItem.java
    OrderStatus.java
    PaymentStatus.java
  dto/
    request/
      CreateOrderRequest.java
      RejectOrderRequest.java
    response/
      OrderResponse.java
      OrderDetailsResponse.java
      OrderTimelineResponse.java
  repository/
    OrderRepository.java
    OrderItemRepository.java
  mapper/
    OrderMapper.java
  exception/
    OrderNotFoundException.java
    InvalidOrderStatusException.java
```

---

## 8. Backend Layering Best Practices

Use:

```text
Controller → Service → Domain Logic → Repository → Database
```

### Controller Layer

Responsible for:

- Receiving HTTP requests.
- Validating request body.
- Calling service methods.
- Returning DTOs.

Do not put business logic here.

### Service Layer

Responsible for:

- Business rules.
- Status transitions.
- Permission checks.
- Transaction boundaries.
- Calling other modules.

### Repository Layer

Responsible for:

- Database access.
- Queries.
- Persistence.

Do not put business logic here.

### DTO Layer

Responsible for:

- Request validation.
- Response shape.
- Hiding internal database fields.

Never expose JPA entities directly to the frontend.

---

## 9. Naming Conventions

### Java Classes

```text
UserController
UserService
UserRepository
UserMapper
UserResponse
CreateUserRequest
UpdateUserRequest
UserNotFoundException
```

### REST Endpoints

Use plural resources:

```http
/api/v1/users
/api/v1/stores
/api/v1/orders
/api/v1/deliveries
```

Avoid:

```http
/getStores
/createNewOrder
/doDeliveryAction
```

### Database Tables

Use snake_case plural:

```text
users
stores
order_items
service_requests
inventory_movements
```

### Java Fields

Use camelCase:

```text
createdAt
updatedAt
paymentStatus
storeId
```

---

## 10. Core Backend Models

### 10.1 User

```text
User
- id
- fullName
- phone
- email
- passwordHash
- status
- createdAt
- updatedAt
```

Rules:

- Phone should be unique if used for login.
- Email can be optional at first.
- Password must always be hashed.
- Never return passwordHash.

---

### 10.2 Role

```text
Role
- id
- name
- description
```

Roles:

```text
ADMIN
SUPPORT_AGENT
CLIENT
STORE_OWNER
STORE_STAFF
STORE_DRIVER
PROVIDER_OWNER
PROVIDER_STAFF
INDEPENDENT_DRIVER
```

---

### 10.3 UserRole

```text
UserRole
- id
- userId
- role
- entityType
- entityId
- createdAt
```

This allows multi-role accounts.

Example:

```text
userId: 10
role: STORE_OWNER
entityType: STORE
entityId: 5
```

---

### 10.4 Store

```text
Store
- id
- ownerUserId
- name
- description
- categoryId
- phone
- address
- latitude
- longitude
- deliveryMode
- status
- openingHours
- minimumOrderAmount
- averagePreparationMinutes
- ratingAverage
- ratingCount
- createdAt
- updatedAt
```

Enums:

```text
StoreStatus:
PENDING_APPROVAL
ACTIVE
SUSPENDED
REJECTED

DeliveryMode:
OWN_DRIVER
PLATFORM_DRIVER
BOTH
PICKUP_ONLY
```

---

### 10.5 Product

```text
Product
- id
- storeId
- categoryId
- name
- description
- price
- imageUrl
- isAvailable
- stockStatus
- createdAt
- updatedAt
```

Product price can change, so orders must store price snapshots.

---

### 10.6 Inventory

```text
Inventory
- id
- storeId
- productId
- quantity
- lowStockThreshold
- reservedQuantity
- updatedAt
```

`reservedQuantity` can be added if you want to reserve stock before completion.

---

### 10.7 InventoryMovement

```text
InventoryMovement
- id
- storeId
- productId
- movementType
- quantityChange
- previousQuantity
- newQuantity
- reason
- referenceType
- referenceId
- createdByUserId
- createdAt
```

Movement types:

```text
STOCK_ADDED
STOCK_REMOVED
ORDER_SALE
ORDER_CANCELLED
MANUAL_ADJUSTMENT
DAMAGED
RETURNED
EXPIRED
```

---

### 10.8 Order

```text
Order
- id
- orderNumber
- clientUserId
- storeId
- status
- subtotal
- deliveryFee
- discount
- total
- paymentMethod
- paymentStatus
- fulfillmentType
- scheduledFor
- notes
- addressSnapshot
- createdAt
- updatedAt
```

Order statuses:

```text
PENDING
ACCEPTED_BY_STORE
REJECTED_BY_STORE
PREPARING
READY_FOR_PICKUP
DRIVER_ASSIGNED
PICKED_UP
ON_THE_WAY
DELIVERED
COMPLETED
CANCELLED
ISSUE_REPORTED
```

---

### 10.9 OrderItem

```text
OrderItem
- id
- orderId
- productId
- productNameSnapshot
- productImageSnapshot
- quantity
- unitPriceSnapshot
- totalPrice
```

Snapshots matter because old receipts should stay correct if product data changes later.

---

### 10.10 ServiceProvider

```text
ServiceProvider
- id
- ownerUserId
- name
- description
- categoryId
- phone
- address
- latitude
- longitude
- status
- openingHours
- responseTimeMinutes
- ratingAverage
- ratingCount
- supportsPickupDelivery
- createdAt
- updatedAt
```

---

### 10.11 Service

```text
Service
- id
- providerId
- categoryId
- name
- description
- pricingType
- basePrice
- isAvailable
- createdAt
- updatedAt
```

Pricing types:

```text
FIXED
STARTING_FROM
HOURLY
AFTER_INSPECTION
CUSTOM_QUOTE
```

---

### 10.12 ServiceRequest

```text
ServiceRequest
- id
- requestNumber
- clientUserId
- providerId
- serviceId
- status
- description
- quoteAmount
- finalAmount
- paymentMethod
- paymentStatus
- deliveryRequired
- preferredTime
- addressSnapshot
- createdAt
- updatedAt
```

---

### 10.13 Driver

```text
Driver
- id
- userId
- driverType
- vehicleType
- phone
- status
- isAvailable
- currentLatitude
- currentLongitude
- ratingAverage
- ratingCount
- completedDeliveriesCount
- createdAt
- updatedAt
```

Driver types:

```text
INDEPENDENT
STORE_DRIVER
PROVIDER_DRIVER
```

---

### 10.14 Delivery

```text
Delivery
- id
- deliveryNumber
- deliveryType
- orderId
- serviceRequestId
- driverId
- status
- pickupName
- pickupAddress
- pickupLatitude
- pickupLongitude
- dropoffName
- dropoffAddress
- dropoffLatitude
- dropoffLongitude
- fee
- distanceKm
- estimatedPickupTime
- estimatedDeliveryTime
- etaConfidence
- paymentCollectionRequired
- cashCollectedAmount
- createdAt
- updatedAt
```

Delivery statuses:

```text
WAITING_FOR_DRIVER
DRIVER_ASSIGNED
ARRIVED_AT_PICKUP
PICKED_UP
ON_THE_WAY
ARRIVED_AT_DESTINATION
DELIVERED
FAILED_DELIVERY
CANCELLED
ISSUE_REPORTED
```

---

### 10.15 Payment

```text
Payment
- id
- paymentReference
- paymentType
- orderId
- serviceRequestId
- deliveryId
- amount
- method
- status
- collectedByDriverId
- collectedAmount
- paidAt
- createdAt
```

Payment methods:

```text
CASH_ON_DELIVERY
CARD_LATER
WALLET_LATER
```

Payment statuses:

```text
UNPAID
PENDING_COLLECTION
COLLECTED
PAID
DISPUTED
REFUNDED
CANCELLED
```

---

### 10.16 Receipt

```text
Receipt
- id
- receiptNumber
- receiptType
- orderId
- serviceRequestId
- deliveryId
- clientUserId
- storeId
- providerId
- subtotal
- deliveryFee
- discount
- total
- paymentMethod
- paymentStatus
- generatedAt
```

Receipt types:

```text
STORE_ORDER
SERVICE_REQUEST
CUSTOM_DELIVERY
```

---

### 10.17 Notification

```text
Notification
- id
- userId
- title
- body
- type
- referenceType
- referenceId
- readAt
- createdAt
```

---

### 10.18 SupportTicket

```text
SupportTicket
- id
- userId
- relatedOrderId
- relatedDeliveryId
- relatedServiceRequestId
- subject
- description
- status
- priority
- createdAt
- updatedAt
```

---

### 10.19 AuditLog

```text
AuditLog
- id
- actorUserId
- action
- targetType
- targetId
- oldValue
- newValue
- ipAddress
- userAgent
- createdAt
```

Audit logs are important for:

- Admin actions.
- Payment changes.
- Inventory changes.
- Role changes.
- Store/provider/driver approvals.
- Suspensions.

---

## 11. Database Best Practices

### Use PostgreSQL

PostgreSQL fits this platform because it has relational business data:

- Users to roles.
- Stores to products.
- Products to inventory.
- Orders to order items.
- Orders to deliveries.
- Payments to receipts.
- Providers to service requests.

### Use Flyway Migrations

Rules:

- Never manually edit production schema.
- Every schema change must be a migration.
- Migration files must be committed to Git.
- Never edit old applied migrations.

Examples:

```text
V1__create_users_table.sql
V2__create_stores_table.sql
V3__create_products_and_inventory.sql
```

### Use UUID or ULID IDs

Recommended:

```text
UUID/ULID for internal IDs
Readable numbers for orders/receipts
```

Example:

```text
Database ID: 550e8400-e29b-41d4-a716-446655440000
Order number: ORD-000928
Receipt number: R-000394
```

### Add Important Indexes

```text
users.phone
users.email
stores.status
stores.category_id
products.store_id
orders.client_user_id
orders.store_id
orders.status
deliveries.driver_id
deliveries.status
service_requests.provider_id
notifications.user_id
```

### Avoid Deleting Important Records

Use soft delete for:

- Users.
- Stores.
- Products.
- Orders.
- Receipts.
- Payments.

Never hard-delete financial records.

---

## 12. API Best Practices

### Version APIs

```http
/api/v1
```

### Use REST Naming

Good:

```http
GET /api/v1/stores
GET /api/v1/stores/{storeId}/products
POST /api/v1/client/orders
```

Bad:

```http
/getStores
/createNewOrder
/doDeliveryAction
```

### Consistent Success Response

```json
{
  "success": true,
  "data": {},
  "message": "Success"
}
```

### Consistent Error Response

```json
{
  "success": false,
  "message": "Product is out of stock",
  "code": "PRODUCT_OUT_OF_STOCK",
  "errors": []
}
```

### Pagination

```http
GET /api/v1/stores?page=1&size=20
```

Response:

```json
{
  "items": [],
  "page": 1,
  "size": 20,
  "totalItems": 150,
  "totalPages": 8
}
```

### Filtering

```http
GET /api/v1/admin/orders?status=PENDING&storeId=123&from=2026-05-01&to=2026-05-02
```

### Never Trust Frontend Calculations

Backend must calculate:

- Product prices.
- Subtotals.
- Discounts.
- Delivery fees.
- Total.
- Payment amount.
- Inventory availability.

Frontend only displays estimated values for UX.

---

## 13. Security Best Practices

Security is critical because the app handles:

- User accounts.
- Phone numbers.
- Addresses.
- Locations.
- Order history.
- Delivery activity.
- Business data.
- Cash collection.
- Payments and receipts.

### 13.1 Authentication

Use:

```text
JWT access token
Refresh token
Secure password hashing
```

Best practices:

- Short-lived access tokens.
- Longer-lived but revocable refresh tokens.
- Rotate refresh tokens.
- Hash passwords with BCrypt or Argon2.
- Rate-limit login attempts.
- Add phone OTP later.

### 13.2 Authorization

Use both:

```text
Role-Based Access Control
Entity-Level Access Control
```

Role check:

```text
Is this user a STORE_OWNER?
```

Entity check:

```text
Does this STORE_OWNER own this specific store?
```

Both are required.

### 13.3 Prevent Broken Object Level Authorization

Every endpoint with an ID must verify access.

Example:

```http
GET /api/v1/store-owner/stores/10/orders
```

If the logged-in user owns Store 9, they must not access Store 10.

Correct process:

```text
Check logged-in user
Check role
Check entity ownership
Return data
```

### 13.4 Input Validation

Use Jakarta Bean Validation:

```text
@NotNull
@NotBlank
@Size
@Email
@Positive
@Min
@Max
```

Example:

```java
public class CreateProductRequest {
  @NotBlank
  private String name;

  @NotNull
  @Positive
  private BigDecimal price;
}
```

### 13.5 Protect Sensitive Data

Never expose:

- Password hashes.
- Refresh tokens.
- Internal logs.
- Private admin notes.
- Driver documents.
- Full payment details.
- Unnecessary phone numbers.
- Exact location after order completion.

### 13.6 Secure File Uploads

Rules:

- Validate file type.
- Limit file size.
- Rename files.
- Store outside app server.
- Use object storage.
- Do not trust file extension only.
- Add file scanning later if needed.

### 13.7 Rate Limiting

Rate-limit:

- Login.
- Register.
- OTP sending.
- Password reset.
- Search.
- Public store listing.
- Order creation.
- Driver location updates.

Use Redis-based rate limiting.

### 13.8 Secure Admin Actions

Log admin actions:

- Approving store.
- Suspending driver.
- Changing payment status.
- Refunding later.
- Editing delivery fee.
- Changing user role.

### 13.9 API Security Checklist

Every API should check:

```text
Is the user authenticated?
Does the user have the correct role?
Does the user own or have access to this resource?
Is the input valid?
Is the resource in the correct status for this action?
Should this action be logged?
Should this action send a notification?
```

---

## 14. Privacy Best Practices

### Collect Only What Is Needed

Needed:

- Name.
- Phone.
- Delivery address.
- Order history.
- Payment method.
- Driver location during active delivery.

Avoid unnecessary personal data.

### Location Privacy

Rules:

- Client location is only shared for active orders.
- Driver live location is only shown during active delivery.
- Do not show driver location after delivery ends.
- Do not expose exact client location to unrelated stores/providers/drivers.
- Admin access to location should be limited and logged.

### Data Retention

Recommended:

- Keep orders and receipts for business/legal needs.
- Keep payment records.
- Keep audit logs.
- Keep detailed driver location trail for limited time only.
- Keep support tickets as needed.

### User Data Control Later

Users should be able to:

- Edit profile.
- Edit addresses.
- Delete saved addresses.
- Request account deactivation.
- Request data deletion where legally possible.

### Privacy in Notifications

Good:

```text
Your order is on the way.
```

Avoid:

```text
Your expensive medicine order from X pharmacy is on the way to [full address].
```

---

## 15. Backend Performance Best Practices

### Database Performance

- Add indexes early.
- Avoid N+1 queries.
- Use pagination.
- Use projections for list views.
- Do not load full entities when only summary is needed.
- Avoid huge joins in public endpoints.
- Use optimized queries for dashboards.

### Caching

Use Redis or Caffeine for:

- Categories.
- Store summaries.
- Home page sections.
- Public settings.
- Driver availability.
- OTP codes.
- Rate limits.

Do not cache sensitive data carelessly.

### Async Jobs

Use async processing for:

- Sending notifications.
- Generating receipt PDFs later.
- Sending emails later.
- Analytics aggregation.
- Heavy reports.

### Driver Location Performance

Do not write every driver location update directly to PostgreSQL.

Better:

```text
Driver app sends location
  ↓
Backend stores latest location in Redis
  ↓
Client tracking reads latest location
  ↓
Important delivery milestones are stored in PostgreSQL
```

---

## 16. React Web Dashboard Stack

Used for:

- Store dashboard.
- Service provider dashboard.
- Admin dashboard.

Recommended:

```text
React
TypeScript
Vite
React Router
TanStack Query
Zustand
React Hook Form
Zod
Tailwind CSS
Shadcn UI
Radix UI
Lucide React
Recharts
TanStack Table
Axios or Fetch wrapper
```

### Recommended Choice

```text
React + Vite + TypeScript
```

Use Next.js only if server-side rendering is needed. For dashboards, SSR is usually not necessary.

---

## 17. React Dashboard Folder Structure

### Business Dashboard

```text
business-dashboard/
  src/
    app/
      App.tsx
      router.tsx
      providers.tsx

    assets/

    components/
      ui/
      layout/
      forms/
      tables/
      feedback/

    features/
      auth/
        api/
        components/
        hooks/
        pages/
        schemas/
        types/

      dashboard/
      orders/
      products/
      inventory/
      receipts/
      deliveries/
      services/
      service-requests/
      staff/
      reports/
      settings/

    lib/
      api.ts
      queryClient.ts
      auth.ts
      permissions.ts
      formatters.ts
      constants.ts

    hooks/
    types/
    styles/
    main.tsx
```

### Admin Dashboard

```text
admin-dashboard/
  src/
    app/
    components/
    features/
      auth/
      overview/
      users/
      stores/
      providers/
      drivers/
      orders/
      deliveries/
      receipts/
      payments/
      support/
      zones/
      categories/
      reports/
      settings/
    lib/
    hooks/
    types/
```

---

## 18. React Frontend Best Practices

### Use Feature-Based Structure

Good:

```text
features/orders/
  api/
  components/
  hooks/
  pages/
  types/
```

Avoid throwing everything into huge global folders.

### Use TypeScript Strict Mode

```json
{
  "compilerOptions": {
    "strict": true
  }
}
```

### Server State

Use TanStack Query for:

- Orders.
- Products.
- Inventory.
- Receipts.
- Deliveries.
- Mutations.
- Refetching after status changes.

### UI State

Use Zustand for:

- Sidebar open/closed.
- Selected filters.
- Current role.
- Temporary UI flags.

Avoid Redux unless complexity requires it.

### Forms

Use:

```text
React Hook Form + Zod
```

### UI Components

Build reusable:

- Button.
- Input.
- Modal.
- Data table.
- Status badge.
- Empty state.
- Page header.
- Confirmation dialog.
- Timeline.
- Receipt card.

---

## 19. React Native Mobile Stack

Used for:

- Client app.
- Driver app.

Recommended:

```text
React Native
Expo
TypeScript
React Navigation
TanStack Query
Zustand
React Hook Form
Zod
Expo Secure Store
Expo Location
React Native Maps
Firebase Cloud Messaging
Axios or Fetch wrapper
NativeWind
```

### Expo or React Native CLI

Use **Expo** for fastest MVP unless you already know you need unsupported native modules.

Expo is useful for:

- Faster setup.
- Easier builds.
- Easier testing.
- Push notifications.
- Location features.
- Camera/image support.

---

## 20. React Native Folder Structure

### Client Mobile

```text
client-mobile/
  src/
    app/
      App.tsx
      providers.tsx
      navigation/
        RootNavigator.tsx
        AuthNavigator.tsx
        MainTabNavigator.tsx
        OrderNavigator.tsx

    assets/
      images/
      icons/

    components/
      ui/
      forms/
      cards/
      feedback/
      layout/

    features/
      auth/
        api/
        components/
        hooks/
        screens/
        schemas/
        types/

      home/
      stores/
      products/
      cart/
      checkout/
      orders/
      tracking/
      services/
      custom-delivery/
      receipts/
      favorites/
      profile/
      support/
      notifications/

    lib/
      api.ts
      queryClient.ts
      authStorage.ts
      secureStorage.ts
      permissions.ts
      formatters.ts
      constants.ts

    hooks/
    types/
    theme/
      colors.ts
      spacing.ts
      typography.ts
```

### Driver Mobile

```text
driver-mobile/
  src/
    app/
      App.tsx
      providers.tsx
      navigation/

    components/
      ui/
      delivery/
      maps/
      feedback/

    features/
      auth/
      onboarding/
      availability/
      jobs/
      active-delivery/
      cash-collection/
      earnings/
      history/
      profile/
      support/
      notifications/

    lib/
      api.ts
      location.ts
      queryClient.ts
      secureStorage.ts
      permissions.ts
      formatters.ts

    hooks/
    types/
    theme/
```

---

## 21. React Native Best Practices

### Navigation

Use React Navigation.

Client app:

```text
Auth Stack
Main Tabs
Order Stack
Tracking Stack
Profile Stack
```

Driver app:

```text
Auth Stack
Driver Main Stack
Active Delivery Stack
Earnings Stack
```

### Secure Token Storage

Do not store access tokens in plain AsyncStorage.

Use:

```text
expo-secure-store
react-native-keychain
```

### Location Permissions

Client app:

- Ask location when choosing address or finding nearby stores.

Driver app:

- Ask location when driver goes online.
- Explain why background location may be needed.

### Mobile Performance

- Use FlatList for long lists.
- Avoid rendering huge lists at once.
- Memoize expensive components.
- Compress images.
- Use skeleton loading states.
- Avoid unnecessary global state.
- Keep maps screen optimized.

### Bad Network Handling

Lebanon may have unstable connectivity, so handle:

- Slow network.
- Failed request retry.
- Offline message.
- Cached previous data.
- Driver action retry for status updates.

Driver actions should be retry-safe.

---

## 22. Shared Frontend Models

Use shared TypeScript types.

```text
packages/shared-types/
  src/
    user.ts
    store.ts
    product.ts
    inventory.ts
    order.ts
    delivery.ts
    service-provider.ts
    service-request.ts
    payment.ts
    receipt.ts
    notification.ts
    support.ts
```

Example:

```ts
export type OrderStatus =
  | 'PENDING'
  | 'ACCEPTED_BY_STORE'
  | 'PREPARING'
  | 'READY_FOR_PICKUP'
  | 'DRIVER_ASSIGNED'
  | 'PICKED_UP'
  | 'ON_THE_WAY'
  | 'DELIVERED'
  | 'COMPLETED'
  | 'CANCELLED';

export interface OrderSummary {
  id: string;
  orderNumber: string;
  status: OrderStatus;
  total: number;
  createdAt: string;
}
```

Frontend types should match backend response DTOs, not database entities.

---

## 23. Frontend API Client Best Practices

Create one API wrapper:

```text
lib/api.ts
```

Responsibilities:

- Set base URL.
- Attach access token.
- Handle refresh token.
- Handle errors.
- Normalize response.
- Redirect to login if unauthorized.

Avoid random fetch/axios calls across the codebase.

---

## 24. Frontend Security Best Practices

### Do Not Trust Frontend Permissions Alone

Frontend can hide buttons for UX, but backend must enforce security.

Example:

- Frontend hides “Delete Product”.
- Backend still rejects unauthorized delete requests.

### Token Storage

Web dashboard:

- Prefer secure HTTP-only cookies if possible.
- If using localStorage, understand XSS risk.
- Use short-lived access tokens.

Mobile:

- Use secure storage.
- Do not store sensitive info in AsyncStorage.

### Hide Sensitive UI Data

Only show phone numbers/contact details when needed for active orders/deliveries.

---

## 25. Frontend Performance Best Practices

### Web Dashboards

- Use code splitting.
- Lazy-load heavy pages.
- Paginate data tables.
- Debounce search inputs.
- Use server-side filtering for large data.
- Avoid loading all orders/products at once.
- Optimize images.

### Mobile Apps

- Use FlatList.
- Avoid inline heavy functions in list items.
- Cache images.
- Optimize maps.
- Avoid too many live location re-renders.
- Use background tasks carefully.
- Batch status updates where possible.

---

## 26. UI Libraries

### React Dashboard UI

Recommended:

```text
Tailwind CSS
Shadcn UI
Radix UI
Lucide React
Recharts
TanStack Table
```

### React Native UI

Recommended:

```text
NativeWind + custom components
```

Alternative options:

```text
React Native Paper
Tamagui
Gluestack UI
```

NativeWind is good because it keeps mobile styling close to Tailwind.

---

## 27. Form and Validation Libraries

Backend:

```text
Jakarta Bean Validation
Custom validators
```

React Web:

```text
React Hook Form
Zod
```

React Native:

```text
React Hook Form
Zod
```

Best rule:

```text
Frontend validates for UX.
Backend validates for security and correctness.
```

Never rely only on frontend validation.

---

## 28. Maps and Location

React Native with Expo:

```text
expo-location
react-native-maps
```

Bare React Native:

```text
react-native-geolocation-service
react-native-maps
```

Web Dashboards:

```text
Google Maps JavaScript API
Mapbox GL JS
Leaflet with OpenStreetMap
```

Backend maps usage:

- Distance estimation.
- ETA estimation.
- Geocoding.
- Reverse geocoding.

Do not overuse paid map APIs. Cache common results where allowed.

---

## 29. Notifications

Use Firebase Cloud Messaging.

Notification types:

```text
ORDER_STATUS_UPDATED
NEW_ORDER_FOR_STORE
NEW_DELIVERY_JOB
DRIVER_ASSIGNED
DELIVERY_STATUS_UPDATED
QUOTE_SENT
PAYMENT_COLLECTED
SUPPORT_TICKET_UPDATED
```

Best practices:

- Store notifications in database.
- Send push asynchronously.
- Do not block order creation if push fails.
- Keep notification text privacy-safe.
- Add notification preferences later.

---

## 30. Realtime Strategy

Options:

```text
Polling
Spring WebSocket + STOMP
Server-Sent Events
```

MVP strategy:

```text
Order tracking: polling every 10-15 seconds
Driver active delivery: location update every 5-10 seconds
Later: WebSockets for smoother realtime
```

Use Redis for latest driver location.

---

## 31. Testing Best Practices

### Backend Testing

Use:

```text
JUnit 5
Mockito
Spring Boot Test
Testcontainers
MockMvc
```

Critical tests:

```text
Client cannot access another client's order.
Store cannot access another store's inventory.
Driver cannot update unassigned delivery.
Inventory decreases when order accepted.
Inventory restores when order cancelled before pickup.
Receipt keeps old price snapshot.
Cash collection updates payment status.
```

### Frontend Testing

Use:

```text
Vitest
React Testing Library
Playwright
Jest
React Native Testing Library
Detox later
```

Critical frontend tests:

- Login flow.
- Order creation.
- Store accepts order.
- Driver updates delivery.
- Admin approves store/driver.

---

## 32. Logging, Monitoring, and Observability

Backend:

```text
Spring Boot Actuator
Micrometer
OpenTelemetry
Structured JSON logs
Sentry or similar error tracking
Prometheus/Grafana later
```

Log:

- Request ID.
- User ID if available.
- Endpoint.
- Error code.
- Latency.
- Important business actions.

Do not log:

- Passwords.
- Tokens.
- Full payment info.
- Sensitive private messages.

Frontend:

```text
Sentry
Console logs only in development
```

Track:

- App crashes.
- Failed API calls.
- Slow screens.
- Delivery tracking errors.

---

## 33. Deployment Best Practices

### Environments

```text
local
development
staging
production
```

Never test risky changes directly in production.

### Backend Deployment

Early setup:

```text
Dockerized Spring Boot app
Managed PostgreSQL
Managed Redis
Object storage
```

### Frontend Deployment

Dashboards:

```text
Vercel
Netlify
Cloudflare Pages
```

Mobile:

```text
Expo EAS Build or native build pipeline
```

### Secrets

Never commit secrets.

Use environment variables:

```text
DATABASE_URL
REDIS_URL
JWT_SECRET
JWT_REFRESH_SECRET
FCM_SERVER_KEY
MAPS_API_KEY
S3_ACCESS_KEY
S3_SECRET_KEY
S3_BUCKET
```

---

## 34. CI/CD Best Practices

Use GitHub Actions or GitLab CI.

Pipeline:

```text
Install dependencies
Lint
Type check
Run tests
Build backend
Build dashboards
Run backend integration tests
Build Docker image
Deploy to staging
Manual approval for production
```

Mobile:

```text
Lint
Type check
Unit tests
EAS build or native build
Internal testing release
```

---

## 35. Code Quality Best Practices

### Backend

- Use formatter.
- Use Checkstyle or Spotless.
- Keep services focused.
- Avoid huge classes.
- Avoid circular dependencies.
- Use transactions carefully.
- Use DTOs, not entities, in API responses.
- Keep business rules close to domain services.

### Frontend

- Use ESLint.
- Use Prettier.
- Use TypeScript strict mode.
- Avoid huge components.
- Extract reusable UI components.
- Keep feature logic inside feature folders.
- Avoid duplicated API code.

---

## 36. Status Transition Best Practices

Never allow random status changes.

Bad:

```text
WAITING_FOR_DRIVER → DELIVERED
```

Correct:

```text
WAITING_FOR_DRIVER
  ↓
DRIVER_ASSIGNED
  ↓
ARRIVED_AT_PICKUP
  ↓
PICKED_UP
  ↓
ON_THE_WAY
  ↓
ARRIVED_AT_DESTINATION
  ↓
DELIVERED
```

Create validators:

```text
OrderStatusService
DeliveryStatusService
ServiceRequestStatusService
PaymentStatusService
```

---

## 37. Inventory Transaction Best Practices

When store accepts order:

```text
Start transaction
  ↓
Lock inventory rows for ordered products
  ↓
Check stock quantity
  ↓
Decrease stock
  ↓
Create inventory movement
  ↓
Update order status
  ↓
Commit transaction
```

If any step fails, rollback everything.

This avoids selling unavailable products.

---

## 38. Payment and Receipt Best Practices

### Payment

- Backend controls payment amount.
- Driver can only mark payment collected for assigned delivery.
- Store/admin can see cash collection records.
- Cash mismatch should create issue flag.

### Receipt

- Receipt generated from backend records.
- Receipt stores snapshots.
- Receipt does not change if product price changes later.
- Receipt number is unique.

---

## 39. Admin Best Practices

Admin dashboard must be powerful but safe.

Rules:

- Admin actions must be logged.
- Dangerous actions need confirmation.
- Support agents should have limited permissions.
- Financial changes require higher permission.
- Admin filters should be strong.
- Admin sees issue alerts early.

Important admin cards:

```text
Delayed orders
Failed deliveries
Cash not settled
Stores with frequent issues
Drivers with repeated failures
Open support tickets
Pending approvals
```

---

## 40. MVP Library List

### Backend MVP

```text
Java 21+
Spring Boot
Spring Web
Spring Security
Spring Data JPA
PostgreSQL Driver
Flyway
Bean Validation
MapStruct
Springdoc OpenAPI
Spring Boot Actuator
Redis
Firebase Admin SDK
JUnit 5
Testcontainers
Docker
```

### React Dashboard MVP

```text
React
TypeScript
Vite
React Router
TanStack Query
Zustand
React Hook Form
Zod
Tailwind CSS
Shadcn UI
TanStack Table
Recharts
Lucide React
Axios
```

### React Native MVP

```text
React Native
Expo
TypeScript
React Navigation
TanStack Query
Zustand
React Hook Form
Zod
Expo Secure Store
Expo Location
React Native Maps
Firebase Cloud Messaging
Axios
NativeWind
```

---

## 41. What Not to Overbuild in MVP

Avoid at first:

```text
Microservices
Event sourcing
Kafka
Kubernetes
Complex wallet system
Advanced AI recommendations
Full route optimization
Complex fraud engine
Advanced analytics warehouse
Multi-store checkout
Full in-app chat
```

Focus on:

```text
Authentication
Authorization
Store management
Product and inventory management
Client ordering
Delivery assignment
Driver status updates
Cash collection
Receipts
Admin monitoring
```

---

## 42. Recommended Development Order

### Step 1: Project Setup

- Monorepo setup.
- Spring Boot backend setup.
- React dashboard setup.
- React Native app setup.
- Shared TypeScript types.
- Docker Compose for local PostgreSQL and Redis.

### Step 2: Backend Foundation

- Users.
- Auth.
- Roles.
- Permissions.
- Audit logs.
- API error format.

### Step 3: Store + Product + Inventory

- Store model.
- Product model.
- Inventory model.
- Inventory movements.
- Stock status.

### Step 4: Client Order Flow

- Store listing.
- Product listing.
- Cart checkout.
- Create order.
- Order details.

### Step 5: Store Order Management

- Accept/reject order.
- Preparing/ready.
- Inventory transaction.
- Request delivery.

### Step 6: Delivery Flow

- Driver profile.
- Driver availability.
- Available jobs.
- Accept delivery.
- Delivery status updates.
- Cash collection.

### Step 7: Receipts and Payments

- Receipt generation.
- Payment status update.
- Receipt details.

### Step 8: Admin Dashboard

- Approvals.
- Orders.
- Deliveries.
- Receipts.
- Cash monitoring.

### Step 9: Service Provider Flow

- Provider profile.
- Services.
- Service requests.
- Quotes.
- Service receipts.

---

## 43. Final Engineering Rules

```text
Use one shared backend.
Start as a modular monolith.
Use PostgreSQL for business data.
Use Redis for temporary/realtime data.
Use role-based and entity-level authorization.
Never expose database entities directly.
Never trust frontend totals or permissions.
Use transactions for inventory and payment changes.
Use snapshots for order items and receipts.
Log admin and financial actions.
Keep mobile apps fast and simple.
Use feature-based folder structure.
Build MVP first, advanced features later.
```

---

## 44. Final Stack Summary

```text
Backend:
Java + Spring Boot + Spring Security + Spring Data JPA + PostgreSQL + Redis

Web:
React + TypeScript + Vite + TanStack Query + Zustand + Tailwind + Shadcn UI

Mobile:
React Native + Expo + TypeScript + React Navigation + TanStack Query + Zustand

Database:
PostgreSQL + Flyway migrations

Realtime:
Polling first, then Spring WebSocket / STOMP

Notifications:
Firebase Cloud Messaging

Storage:
S3-compatible storage

Maps:
Google Maps or Mapbox

Testing:
JUnit 5, Testcontainers, React Testing Library, Playwright, React Native Testing Library

Monitoring:
Spring Actuator, Micrometer, OpenTelemetry, Sentry
```

This structure gives the project a strong foundation without making the MVP too complicated.
