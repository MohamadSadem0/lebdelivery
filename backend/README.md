# Backend

Spring Boot modular monolith for the Lebanon Local Commerce & Delivery Platform.

## Stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway
- Bean Validation
- Actuator

## Run Locally

From the monorepo root:

```bash
cp .env.example .env
cp backend/.env.example backend/.env
docker compose up -d postgres redis
```

On PowerShell:

```powershell
Copy-Item .env.example .env
Copy-Item backend/.env.example backend/.env
docker compose up -d postgres redis
```

Run the backend from this directory:

```bash
cd backend
mvn spring-boot:run
```

Or run the backend from the monorepo root with Docker Compose:

```bash
docker compose --profile backend up -d backend
```

This path uses a Maven/JDK 21 container and is useful on machines that do not have Java 21 and Maven installed locally.

Health:

```text
GET http://localhost:8080/actuator/health
```

OpenAPI:

```text
GET http://localhost:8080/swagger-ui/index.html
GET http://localhost:8080/v3/api-docs
```

## Main Routes

Authentication:

```text
POST /api/v1/auth/register
POST /api/v1/auth/login
POST /api/v1/auth/refresh
POST /api/v1/auth/logout
GET /api/v1/auth/me
POST /api/v1/auth/active-role
```

Current user profile:

```text
GET /api/v1/users/me
PATCH /api/v1/users/me
```

Client addresses:

```text
GET /api/v1/client/addresses
POST /api/v1/client/addresses
GET /api/v1/client/addresses/{addressId}
PATCH /api/v1/client/addresses/{addressId}
POST /api/v1/client/addresses/{addressId}/default
DELETE /api/v1/client/addresses/{addressId}
```

Store types:

```text
GET /api/v1/store-types
GET /api/v1/store-types/{code}/config
```

Public store/product browsing:

```text
GET /api/v1/stores
GET /api/v1/stores/{storeId}
GET /api/v1/stores/{storeId}/products
GET /api/v1/stores/{storeId}/products/{productId}
GET /api/v1/stores/{storeId}/product-form-config
```

Store owner:

```text
POST /api/v1/store-owner/stores
GET /api/v1/store-owner/stores
GET /api/v1/store-owner/stores/{storeId}
PATCH /api/v1/store-owner/stores/{storeId}
GET /api/v1/store-owner/stores/{storeId}/products
POST /api/v1/store-owner/stores/{storeId}/products
PATCH /api/v1/store-owner/stores/{storeId}/products/{productId}
DELETE /api/v1/store-owner/stores/{storeId}/products/{productId}
GET /api/v1/store-owner/stores/{storeId}/inventory
POST /api/v1/store-owner/stores/{storeId}/inventory/{productId}/adjust
GET /api/v1/store-owner/stores/{storeId}/inventory-movements
```

Client orders:

```text
POST /api/v1/client/orders
GET /api/v1/client/orders
GET /api/v1/client/orders/{orderId}
GET /api/v1/client/orders/{orderId}/timeline
POST /api/v1/client/orders/{orderId}/cancel
POST /api/v1/client/orders/{orderId}/reorder
```

Store order management:

```text
GET /api/v1/store-owner/stores/{storeId}/orders
GET /api/v1/store-owner/stores/{storeId}/orders/{orderId}
POST /api/v1/store-owner/stores/{storeId}/orders/{orderId}/accept
POST /api/v1/store-owner/stores/{storeId}/orders/{orderId}/reject
POST /api/v1/store-owner/stores/{storeId}/orders/{orderId}/mark-preparing
POST /api/v1/store-owner/stores/{storeId}/orders/{orderId}/mark-ready
```

Delivery and driver MVP:

```text
POST /api/v1/driver/profile
GET /api/v1/driver/profile
GET /api/v1/driver/jobs/available
GET /api/v1/driver/jobs/{deliveryId}
POST /api/v1/driver/jobs/{deliveryId}/accept
GET /api/v1/driver/deliveries/active
GET /api/v1/driver/deliveries/history
GET /api/v1/driver/earnings
POST /api/v1/driver/deliveries/{deliveryId}/arrived-pickup
POST /api/v1/driver/deliveries/{deliveryId}/picked-up
POST /api/v1/driver/deliveries/{deliveryId}/on-the-way
POST /api/v1/driver/deliveries/{deliveryId}/arrived-destination
POST /api/v1/driver/deliveries/{deliveryId}/delivered
POST /api/v1/driver/deliveries/{deliveryId}/failed
POST /api/v1/driver/deliveries/{deliveryId}/cash-collected
POST /api/v1/store-owner/stores/{storeId}/orders/{orderId}/delivery-request
GET /api/v1/store-owner/stores/{storeId}/deliveries
GET /api/v1/store-owner/stores/{storeId}/deliveries/{deliveryId}
```

Receipts:

```text
GET /api/v1/client/receipts
GET /api/v1/client/receipts/{receiptId}
GET /api/v1/store-owner/stores/{storeId}/receipts
GET /api/v1/store-owner/stores/{storeId}/receipts/{receiptId}
GET /api/v1/admin/receipts
GET /api/v1/admin/receipts/{receiptId}
```

Service providers:

```text
GET /api/v1/service-providers
GET /api/v1/service-providers/{providerId}
GET /api/v1/service-providers/{providerId}/services
POST /api/v1/provider-owner/providers
GET /api/v1/provider-owner/providers
GET /api/v1/provider-owner/providers/{providerId}
PATCH /api/v1/provider-owner/providers/{providerId}
GET /api/v1/provider-owner/providers/{providerId}/services
POST /api/v1/provider-owner/providers/{providerId}/services
PATCH /api/v1/provider-owner/providers/{providerId}/services/{serviceId}
POST /api/v1/client/service-requests
GET /api/v1/client/service-requests
GET /api/v1/client/service-requests/{requestId}
POST /api/v1/client/service-requests/{requestId}/accept-quote
POST /api/v1/client/service-requests/{requestId}/cancel
GET /api/v1/provider-owner/providers/{providerId}/service-requests
GET /api/v1/provider-owner/providers/{providerId}/service-requests/{requestId}
POST /api/v1/provider-owner/providers/{providerId}/service-requests/{requestId}/accept
POST /api/v1/provider-owner/providers/{providerId}/service-requests/{requestId}/reject
POST /api/v1/provider-owner/providers/{providerId}/service-requests/{requestId}/quote
POST /api/v1/provider-owner/providers/{providerId}/service-requests/{requestId}/mark-in-progress
POST /api/v1/provider-owner/providers/{providerId}/service-requests/{requestId}/mark-ready
POST /api/v1/provider-owner/providers/{providerId}/service-requests/{requestId}/mark-completed
```

Notifications:

```text
GET /api/v1/notifications
GET /api/v1/notifications/unread-count
POST /api/v1/notifications/{notificationId}/read
POST /api/v1/notifications/read-all
```

Support:

```text
POST /api/v1/support/tickets
GET /api/v1/support/tickets
GET /api/v1/support/tickets/{ticketId}
GET /api/v1/admin/support/tickets
GET /api/v1/admin/support/tickets/{ticketId}
PATCH /api/v1/admin/support/tickets/{ticketId}/status
```

Admin marketplace controls:

```text
GET /api/v1/admin/users
POST /api/v1/admin/users/{userId}/activate
POST /api/v1/admin/users/{userId}/suspend
GET /api/v1/admin/stores
POST /api/v1/admin/stores/{storeId}/approve
POST /api/v1/admin/stores/{storeId}/reject
POST /api/v1/admin/stores/{storeId}/suspend
GET /api/v1/admin/drivers
POST /api/v1/admin/drivers/{driverId}/approve
POST /api/v1/admin/drivers/{driverId}/reject
POST /api/v1/admin/drivers/{driverId}/suspend
GET /api/v1/admin/orders
GET /api/v1/admin/orders/{orderId}
GET /api/v1/admin/deliveries
GET /api/v1/admin/deliveries/{deliveryId}
GET /api/v1/admin/payments
GET /api/v1/admin/payments/{paymentId}
GET /api/v1/admin/cash-settlements
POST /api/v1/admin/cash-settlements
```

## cURL Examples

Register:

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Mohamad Serhal","phone":"+96100000000","email":"mohamad@example.com","password":"StrongPassword123"}'
```

Login:

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"+96100000000","password":"StrongPassword123"}'
```

Save client address:

```bash
curl -X POST http://localhost:8080/api/v1/client/addresses \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $CLIENT_ACCESS_TOKEN" \
  -d '{"label":"Home","fullAddress":"Hamra, Beirut","phoneNumber":"+96100000000","instructions":"Call before arriving","defaultAddress":true}'
```

Create store:

```bash
curl -X POST http://localhost:8080/api/v1/store-owner/stores \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -d '{"name":"Hamra Mini Market","description":"Local grocery and daily essentials","storeTypeCode":"GROCERY","phone":"+96100000000","address":"Hamra, Beirut","latitude":33.895,"longitude":35.482,"deliveryMode":"BOTH","openingHours":{}}'
```

Opening hours JSON supports simple day keys with one or more windows. Empty `{}` currently means always open for MVP compatibility:

```json
{
  "timezone": "Asia/Beirut",
  "monday": [{ "open": "09:00", "close": "18:00" }],
  "tuesday": [{ "open": "09:00", "close": "18:00" }],
  "wednesday": [{ "open": "09:00", "close": "18:00" }],
  "thursday": [{ "open": "09:00", "close": "18:00" }],
  "friday": [{ "open": "09:00", "close": "18:00" }],
  "saturday": [{ "open": "10:00", "close": "15:00" }],
  "sunday": []
}
```

Overnight windows are supported, for example `{ "open": "20:00", "close": "02:00" }`.

Create product:

```bash
curl -X POST http://localhost:8080/api/v1/store-owner/stores/$STORE_ID/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -d '{"name":"Pepsi 330ml","description":"Cold soft drink","price":0.75,"isAvailable":true,"attributes":[{"key":"brand","value":"Pepsi"},{"key":"package_size","value":"330ml"}],"initialInventory":{"quantity":120,"lowStockThreshold":15,"unitType":"PIECE"}}'
```

Create order:

```bash
curl -X POST http://localhost:8080/api/v1/client/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $CLIENT_ACCESS_TOKEN" \
  -d '{"storeId":"'$STORE_ID'","paymentMethod":"CASH_ON_DELIVERY","fulfillmentType":"NOW","scheduledFor":null,"needsChange":true,"cashAmountClientHas":20.00,"notes":"Call before arriving","address":{"label":"Home","fullAddress":"Hamra, Beirut","latitude":33.895,"longitude":35.482,"phoneNumber":"+96100000000"},"items":[{"productId":"'$PRODUCT_ID'","productVariantId":null,"quantity":2}]}'
```

Create a scheduled order:

```bash
curl -X POST http://localhost:8080/api/v1/client/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $CLIENT_ACCESS_TOKEN" \
  -d '{"storeId":"'$STORE_ID'","paymentMethod":"CASH_ON_DELIVERY","fulfillmentType":"SCHEDULED","scheduledFor":"'$SCHEDULED_FOR_ISO'","needsChange":false,"notes":"Scheduled delivery","addressId":"'$ADDRESS_ID'","items":[{"productId":"'$PRODUCT_ID'","quantity":1}]}'
```

Create order using a saved address:

```bash
curl -X POST http://localhost:8080/api/v1/client/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $CLIENT_ACCESS_TOKEN" \
  -d '{"storeId":"'$STORE_ID'","paymentMethod":"CASH_ON_DELIVERY","fulfillmentType":"NOW","needsChange":true,"cashAmountClientHas":20.00,"notes":"Call before arriving","addressId":"'$ADDRESS_ID'","items":[{"productId":"'$PRODUCT_ID'","productVariantId":null,"quantity":2}]}'
```

Reorder using current prices and stock:

```bash
curl -X POST http://localhost:8080/api/v1/client/orders/$ORDER_ID/reorder \
  -H "Authorization: Bearer $CLIENT_ACCESS_TOKEN"
```

Accept order:

```bash
curl -X POST http://localhost:8080/api/v1/store-owner/stores/$STORE_ID/orders/$ORDER_ID/accept \
  -H "Authorization: Bearer $STORE_OWNER_ACCESS_TOKEN"
```

Mark preparing:

```bash
curl -X POST http://localhost:8080/api/v1/store-owner/stores/$STORE_ID/orders/$ORDER_ID/mark-preparing \
  -H "Authorization: Bearer $STORE_OWNER_ACCESS_TOKEN"
```

Mark ready:

```bash
curl -X POST http://localhost:8080/api/v1/store-owner/stores/$STORE_ID/orders/$ORDER_ID/mark-ready \
  -H "Authorization: Bearer $STORE_OWNER_ACCESS_TOKEN"
```

Request delivery:

```bash
curl -X POST http://localhost:8080/api/v1/store-owner/stores/$STORE_ID/orders/$ORDER_ID/delivery-request \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $STORE_OWNER_ACCESS_TOKEN" \
  -d '{"deliveryFee":0,"pickupAddress":"Store address","dropoffAddress":"Client address"}'
```

Create driver profile:

```bash
curl -X POST http://localhost:8080/api/v1/driver/profile \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $DRIVER_ACCESS_TOKEN" \
  -d '{"driverType":"INDEPENDENT","vehicleType":"Motorbike"}'
```

Accept delivery job:

```bash
curl -X POST http://localhost:8080/api/v1/driver/jobs/$DELIVERY_ID/accept \
  -H "Authorization: Bearer $DRIVER_ACCESS_TOKEN"
```

Mark delivery on the way:

```bash
curl -X POST http://localhost:8080/api/v1/driver/deliveries/$DELIVERY_ID/on-the-way \
  -H "Authorization: Bearer $DRIVER_ACCESS_TOKEN"
```

Mark cash collected and generate receipt:

```bash
curl -X POST http://localhost:8080/api/v1/driver/deliveries/$DELIVERY_ID/cash-collected \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $DRIVER_ACCESS_TOKEN" \
  -d '{"collectedAmount":12.50,"needsChange":false,"note":"Client paid exact amount"}'
```

Approve a store as admin:

```bash
curl -X POST http://localhost:8080/api/v1/admin/stores/$STORE_ID/approve \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_ACCESS_TOKEN" \
  -d '{"reason":"Documents verified"}'
```

Suspend a driver as admin:

```bash
curl -X POST http://localhost:8080/api/v1/admin/drivers/$DRIVER_ID/suspend \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_ACCESS_TOKEN" \
  -d '{"reason":"Manual review required"}'
```

List admin cash/payment records:

```bash
curl http://localhost:8080/api/v1/admin/payments \
  -H "Authorization: Bearer $ADMIN_ACCESS_TOKEN"
```

Record driver cash settlement as admin:

```bash
curl -X POST http://localhost:8080/api/v1/admin/cash-settlements \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_ACCESS_TOKEN" \
  -d '{"driverId":"'$DRIVER_ID'","amount":25.00,"note":"Cash handed to office"}'
```

## Security Notes

- APIs return DTOs, not entities.
- Every response includes an `X-Request-Id` header for log correlation.
- OpenAPI documentation is public in local/dev so the API can be inspected quickly.
- Backend calculates prices and controls payment status.
- Store owner order routes check store ownership before returning or mutating orders.
- Client address routes require `CLIENT` and only return the authenticated user's own active addresses.
- Orders can use a saved address, but the order keeps an immutable address snapshot.
- Scheduled orders require `scheduledFor` at least 30 minutes in the future and no more than 14 days ahead.
- Immediate orders require the store to be open now. Scheduled orders require the store to be open at the selected scheduled time.
- Reorder creates a new order only after checking current store status, product availability, inventory, and backend prices.
- Order acceptance reduces inventory inside a transaction with pessimistic inventory locking.
- Delivery job acceptance locks the delivery row so two drivers cannot accept the same job.
- Driver cash collection requires the assigned delivery to be marked delivered.
- Admin cash settlements reduce driver unsettled COD cash and write audit log entries.
- Receipt snapshots are generated from backend order/payment values, not frontend totals.
- Admin marketplace actions require `ADMIN` and write audit log entries.
- Provider owner routes assign and enforce `PROVIDER_OWNER` entity roles for the exact service provider.
- Client service request routes require `CLIENT` and only return the caller's own requests.
- Notification routes return only the authenticated user's own notifications.
- In-app notifications are created for order, delivery, payment, and service request status changes.
- Support ticket routes return only the authenticated user's own tickets; admin/support routes require `ADMIN` or `SUPPORT_AGENT`.
- Support ticket status updates notify the ticket owner and write audit logs.
- Refresh tokens are stored hashed; access and refresh secrets come from environment variables.
- Profile updates create an audit log entry.
