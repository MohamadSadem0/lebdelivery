# 02 — Functionalities, Workflows, Transitions, APIs & Roadmap

## Purpose

This file summarizes the full functional blueprint for the Lebanon Local Commerce & Delivery Platform.

It covers:

- App functionalities.
- App transitions.
- User workflows.
- State machines.
- API structure.
- Realtime events.
- MVP scope.
- Priority roadmap.

The platform has five connected systems:

```text
Client System
Store System
Service Provider System
Delivery System
Admin System
```

---

## 1. Big Picture Flow

```text
Client
  ↓
Places order / requests service / requests custom delivery
  ↓
Store or Service Provider receives request
  ↓
Store/Provider accepts, rejects, prepares, or quotes
  ↓
Delivery is created if needed
  ↓
Driver accepts or is assigned
  ↓
Driver picks up and delivers
  ↓
Payment is confirmed
  ↓
Receipt is generated
  ↓
Admin can monitor everything
```

Main business objects:

```text
Users
Roles
Stores
Products
Inventory
Orders
Service Requests
Deliveries
Payments
Receipts
Notifications
Support Tickets
Audit Logs
```

---

## 2. Client System Functionalities

### 2.1 Account and Profile

- Register.
- Login.
- Logout.
- Forgot password.
- Edit profile.
- Add profile photo later.
- Add addresses.
- Save multiple addresses.
- Choose default address.
- Manage household accounts later.

### 2.2 Home and Discovery

- View nearby stores.
- View nearby restaurants.
- View service providers.
- Search products.
- Search stores.
- Search services.
- View categories.
- View urgent categories.
- View recently ordered stores.
- View fastest delivery.
- View open-now businesses.
- View best-rated near you.
- View popular this week.
- View quick essentials.

### 2.3 Store Ordering

- Open store page.
- View store details.
- View categories.
- View products.
- View stock certainty badges.
- View product details.
- Add item to cart.
- Change item quantity.
- Remove item.
- Add order notes.
- Choose delivery address.
- Choose payment method.
- Choose need-change option for cash.
- Place order.
- Track order timeline.
- See ETA.
- Contact store.
- Contact driver after assignment.
- View receipt.
- Reorder.
- Rate order/store/driver.
- Report issue.

### 2.4 Service Requests

- Browse providers.
- Open provider page.
- View services.
- Choose service.
- Fill request form.
- Add description.
- Upload photo later.
- Choose location.
- Request quote.
- Accept/reject quote.
- Track request.
- Contact provider.
- Request pickup/delivery if needed.
- View receipt.
- Rate provider.

### 2.5 Custom Delivery

- Create pickup/dropoff request.
- Add pickup location.
- Add dropoff location.
- Add item description.
- Add notes.
- See estimated delivery fee.
- Confirm request.
- Track driver.
- Contact driver.
- Confirm delivery.
- View receipt.

### 2.6 Urgent Needs

Urgent categories:

```text
Pharmacy now
Baby items
Water and gas
Urgent groceries
Forgotten item delivery
Document delivery
Key delivery
Repair pickup
Laundry pickup
```

### 2.7 Request Anything / Concierge Mode

The user can ask for something that is not listed.

Flow:

```text
User writes what they need
  ↓
Adds photo/budget if needed
  ↓
System/admin routes request
  ↓
Nearby stores/providers respond
  ↓
User confirms
  ↓
Order/service/delivery is created
```

### 2.8 Help Me Find It

Intent-based search examples:

```text
I need printer ink near me
I need medicine urgently
I need a technician today
I need someone to pick up my phone for repair
```

The system suggests:

- Stores.
- Service providers.
- Custom delivery.
- Urgent request.
- Support/concierge path.

### 2.9 Notifications

Clients receive notifications for:

- Order accepted.
- Order rejected.
- Order preparing.
- Driver assigned.
- Driver picked up order.
- Driver on the way.
- Order delivered.
- Quote received.
- Payment confirmed.
- Issue update.

### 2.10 Receipts and History

- View order history.
- View service request history.
- View delivery history.
- View receipt details.
- Reorder from receipt.
- Report issue from receipt.
- Share/download receipt later.

---

## 3. Store System Functionalities

### 3.1 Store Account and Profile

- Store registration.
- Store login.
- Store profile setup.
- Business name.
- Business category.
- Phone number.
- Address.
- Map location.
- Opening hours.
- Store logo/image.
- Delivery options.
- Approval status.

### 3.2 Product Management

- Add product.
- Edit product.
- Delete product or soft delete.
- Upload product image.
- Assign category.
- Set price.
- Set description.
- Mark available/unavailable.
- Duplicate product.
- Add variants later.
- Bulk import later.

### 3.3 Inventory Management

- Set quantity.
- Add stock.
- Remove stock.
- Manual adjustment.
- Set low-stock alert.
- View low-stock products.
- View out-of-stock products.
- Auto-reduce stock when order is accepted.
- Restore stock when order is cancelled before pickup.
- View movement history.
- Hide unavailable items automatically.
- View best sellers.
- View missed opportunities later.

### 3.4 Order Management

- View new orders.
- Accept order.
- Reject order with reason.
- Mark preparing.
- Mark ready for pickup.
- Assign store driver.
- Request independent driver.
- View order details.
- View order timeline.
- View payment status.
- Contact client.
- Contact driver.
- Complete order.

### 3.5 Receipts

- View receipts.
- Filter by day/week/month.
- View receipt details.
- See cash collected by driver.
- See order total and delivery fee.
- Export/download later.

### 3.6 Delivery Settings

- Store delivery only.
- Independent platform delivery only.
- Both.
- Pickup only.
- Set preparation time.
- Manage store drivers.
- Receive delivery recommendation later.

### 3.7 Staff Management

- Add staff user.
- Remove staff user.
- Assign permissions.
- Presets later:
  - Order manager.
  - Inventory manager.
  - Cashier.
  - Delivery coordinator.
  - Support role.

### 3.8 Reports and Insights

- Daily orders.
- Daily sales.
- Best-selling products.
- Low-stock products.
- Cancelled orders.
- Rejected order reasons.
- Delivery performance.
- Missed sales insights later.
- Frequently searched unavailable items later.
- Products often bought together later.

---

## 4. Service Provider Functionalities

### 4.1 Provider Account and Profile

- Provider registration.
- Provider login.
- Provider profile setup.
- Business/service name.
- Service category.
- Phone number.
- Address.
- Map location.
- Opening hours.
- Approval status.

### 4.2 Service Management

- Add service.
- Edit service.
- Delete service.
- Set description.
- Set pricing type.
- Set base price.
- Mark available/unavailable.

Pricing types:

```text
FIXED
STARTING_FROM
HOURLY
AFTER_INSPECTION
CUSTOM_QUOTE
```

### 4.3 Service Request Management

- View new requests.
- Accept request.
- Reject request.
- Send quote.
- Update quote.
- Mark quote accepted.
- Mark service in progress.
- Mark service ready.
- Mark service completed.
- Contact client.
- Request delivery if needed.

### 4.4 Delivery for Services

- Request pickup from client.
- Request return delivery to client.
- Track delivery.
- Contact driver.
- Confirm item received.
- Confirm item returned.

### 4.5 Receipts and Reports

- Generate service receipt.
- View service receipts.
- View payment status.
- Filter receipts.
- View daily service requests.
- View completed jobs.
- View cancelled jobs.
- View earnings.
- View most requested services.

---

## 5. Delivery System Functionalities

### 5.1 Driver Account and Profile

- Driver registration.
- Driver login.
- Driver profile setup.
- Upload documents later.
- Vehicle type.
- Phone number.
- Availability status.
- Approval status.

### 5.2 Job Discovery

Drivers can see:

- Available delivery jobs.
- Estimated fee.
- Estimated distance.
- Pickup area.
- Dropoff area.
- Cash collection requirement.
- Item type.
- Estimated duration.

### 5.3 Active Delivery

- Accept job.
- Reject job.
- Navigate to pickup.
- Mark arrived at pickup.
- Mark picked up.
- Navigate to dropoff.
- Mark arrived at destination.
- Mark delivered.
- Mark failed delivery.
- Contact client.
- Contact store/provider.
- Contact support.

### 5.4 Cash Collection

- See amount to collect.
- See if client needs change.
- Mark cash collected.
- Enter collected amount.
- Confirm payment.
- View collected cash history.

### 5.5 Delivery Exceptions

Structured issue flows for:

- Customer not answering.
- Wrong address.
- Store delayed.
- Item missing.
- Payment issue.
- Failed delivery reason.

### 5.6 Earnings and Performance

- View delivery earnings.
- View completed deliveries.
- View cancelled/failed deliveries.
- View daily/weekly summary.
- Driver performance score later:
  - On-time rate.
  - Acceptance rate.
  - Completion rate.
  - Customer rating.
  - Cash handling accuracy.
  - Badge level.

### 5.7 Safety and Support

- One-tap support.
- Emergency support later.
- Issue escalation.
- Proof of delivery upload later.

---

## 6. Admin System Functionalities

### 6.1 Overview

Dashboard cards:

- Total users.
- Total stores.
- Total providers.
- Total drivers.
- Active orders.
- Active deliveries.
- Daily revenue.
- Open support tickets.
- Pending approvals.
- Delayed orders.
- Failed deliveries.
- Cash not settled.

### 6.2 User Management

- View users.
- Search users.
- View user details.
- Suspend user.
- Activate user.
- View user roles.
- Manage roles.

### 6.3 Store Management

- View stores.
- Approve store.
- Reject store.
- Suspend store.
- Edit store category.
- View store orders.
- View store receipts.
- View store issues.

### 6.4 Provider Management

- View providers.
- Approve provider.
- Reject provider.
- Suspend provider.
- View provider requests.
- View provider receipts.
- View provider issues.

### 6.5 Driver Management

- View drivers.
- Approve driver.
- Reject driver.
- Suspend driver.
- View active drivers.
- View driver delivery history.
- View driver cash collection.
- View driver performance later.

### 6.6 Order and Delivery Monitoring

- View all orders.
- View all service requests.
- View all deliveries.
- Filter by status.
- Handle failed deliveries.
- Reassign delivery.
- Cancel order if needed.
- Monitor delayed orders.

### 6.7 Finance and Receipts

- View payments.
- View receipts.
- View cash collection.
- Track delivery fees.
- Track commissions later.
- Export reports later.

### 6.8 Platform Settings

- Manage zones.
- Manage categories.
- Manage delivery fees.
- Manage commission rates.
- Manage support reasons.
- Manage cancellation reasons.

### 6.9 Support

- View support tickets.
- Assign ticket to support agent.
- Reply to ticket.
- Close ticket.
- Escalate issue.

### 6.10 Platform Intelligence Later

- Risk flags.
- Fraud warning patterns.
- Quality scores.
- Store/provider response speed.
- Driver delivery speed.
- Support resolution time.

---

## 7. App Transitions and Navigation

### 7.1 Client First-Time User Flow

```text
Splash Screen
  ↓
Onboarding Screens
  ↓
Register / Login
  ↓
Location Permission
  ↓
Add Address
  ↓
Home Screen
```

### 7.2 Client Returning User Flow

```text
Splash Screen
  ↓
Check authentication token
  ↓
If logged in: Home Screen
If not logged in: Login Screen
```

### 7.3 Multi-Role User Flow

```text
Splash Screen
  ↓
Login
  ↓
Role Selection
  ↓
Selected Dashboard
```

Example:

```text
Login
  ↓
Choose: Client / Store Owner / Driver
  ↓
Open selected system
```

### 7.4 Client Store Order Flow

```text
Home
  ↓
Store Listing
  ↓
Store Details
  ↓
Product Details
  ↓
Cart
  ↓
Checkout
  ↓
Order Created
  ↓
Order Tracking
  ↓
Receipt
  ↓
Rating
```

### 7.5 Client Service Request Flow

```text
Home
  ↓
Service Provider Listing
  ↓
Provider Details
  ↓
Service Details
  ↓
Service Request Form
  ↓
Request Tracking
  ↓
Quote Screen, if needed
  ↓
Service Progress
  ↓
Receipt
  ↓
Rating
```

### 7.6 Custom Delivery Flow

```text
Home
  ↓
Custom Delivery Request
  ↓
Pickup Location
  ↓
Dropoff Location
  ↓
Item Details
  ↓
Delivery Fee Estimate
  ↓
Confirm Request
  ↓
Driver Matching
  ↓
Live Tracking
  ↓
Receipt
```

### 7.7 Store Dashboard New Order Flow

```text
Dashboard
  ↓
New Orders
  ↓
Order Details
  ↓
Accept / Reject
  ↓
Preparing
  ↓
Ready for Pickup
  ↓
Assign Driver / Request Driver
  ↓
Delivery Tracking
  ↓
Receipt
```

### 7.8 Store Product and Inventory Flow

```text
Dashboard
  ↓
Products
  ↓
Add/Edit Product
  ↓
Inventory
  ↓
Stock Adjustment
  ↓
Inventory Movement History
```

### 7.9 Provider Request Flow

```text
Provider Dashboard
  ↓
New Requests
  ↓
Request Details
  ↓
Accept / Reject
  ↓
Send Quote, if needed
  ↓
Client Accepts Quote
  ↓
In Progress
  ↓
Ready / Completed
  ↓
Receipt
```

### 7.10 Driver Job Flow

```text
Driver Home
  ↓
Set Available
  ↓
Available Jobs
  ↓
Job Details
  ↓
Accept Job
  ↓
Navigate to Pickup
  ↓
Arrived at Pickup
  ↓
Picked Up
  ↓
Navigate to Dropoff
  ↓
Arrived at Destination
  ↓
Delivered
  ↓
Cash Collection, if required
  ↓
Delivery Completed
  ↓
Earnings Updated
```

### 7.11 Admin Monitoring Flow

```text
Admin Login
  ↓
Admin Overview
  ↓
Orders / Deliveries / Users / Stores / Providers
  ↓
Details Page
  ↓
Take Action
  ↓
Audit Log Updated
```

---

## 8. Workflow State Machines

### 8.1 Product Order State Machine

Normal path:

```text
PENDING
  ↓
ACCEPTED_BY_STORE
  ↓
PREPARING
  ↓
READY_FOR_PICKUP
  ↓
DRIVER_ASSIGNED
  ↓
PICKED_UP
  ↓
ON_THE_WAY
  ↓
DELIVERED
  ↓
COMPLETED
```

Alternative paths:

```text
PENDING → REJECTED_BY_STORE
PENDING → CANCELLED_BY_CLIENT
ACCEPTED_BY_STORE → CANCELLED_BY_STORE_OR_ADMIN
PICKED_UP → FAILED_DELIVERY
ANY_ACTIVE_STATUS → ISSUE_REPORTED
```

### 8.2 Service Request State Machine

Normal path:

```text
PENDING
  ↓
ACCEPTED
  ↓
WAITING_FOR_QUOTE, optional
  ↓
QUOTE_SENT
  ↓
QUOTE_ACCEPTED
  ↓
IN_PROGRESS
  ↓
READY
  ↓
COMPLETED
```

Alternative paths:

```text
PENDING → REJECTED
QUOTE_SENT → QUOTE_REJECTED
ANY_ACTIVE_STATUS → CANCELLED
ANY_ACTIVE_STATUS → ISSUE_REPORTED
```

### 8.3 Delivery State Machine

Normal path:

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

Alternative paths:

```text
WAITING_FOR_DRIVER → CANCELLED
PICKED_UP → FAILED_DELIVERY
ANY_ACTIVE_STATUS → ISSUE_REPORTED
```

### 8.4 Payment State Machine

```text
UNPAID
  ↓
PENDING_COLLECTION
  ↓
COLLECTED
  ↓
PAID
```

Alternative paths:

```text
UNPAID → CANCELLED
COLLECTED → DISPUTED
PAID → REFUNDED, later
```

---

## 9. API Structure

Base API:

```http
/api/v1
```

Examples:

```http
/api/v1/auth/login
/api/v1/stores
/api/v1/orders
/api/v1/deliveries
```

---

## 10. Authentication APIs

```http
POST /api/v1/auth/register
POST /api/v1/auth/login
GET  /api/v1/auth/me
POST /api/v1/auth/refresh
POST /api/v1/auth/logout
```

### Register Body Example

```json
{
  "fullName": "Mohamad Serhal",
  "phone": "+96100000000",
  "email": "mohamad@example.com",
  "password": "password"
}
```

### Login Body Example

```json
{
  "phone": "+96100000000",
  "password": "password"
}
```

---

## 11. User and Role APIs

```http
GET    /api/v1/users/me
PATCH  /api/v1/users/me
GET    /api/v1/users/me/roles
POST   /api/v1/users/me/active-role
```

Admin:

```http
GET    /api/v1/admin/users
GET    /api/v1/admin/users/{id}
PATCH  /api/v1/admin/users/{id}/status
POST   /api/v1/admin/users/{id}/roles
DELETE /api/v1/admin/users/{id}/roles/{roleId}
```

---

## 12. Client APIs

### Addresses

```http
GET    /api/v1/client/addresses
POST   /api/v1/client/addresses
PATCH  /api/v1/client/addresses/{id}
DELETE /api/v1/client/addresses/{id}
POST   /api/v1/client/addresses/{id}/default
```

### Home and Search

```http
GET /api/v1/client/home
GET /api/v1/client/categories
GET /api/v1/client/search?q=keyword
GET /api/v1/client/urgent-categories
```

### Favorites and Saved Lists Later

```http
GET    /api/v1/client/favorites
POST   /api/v1/client/favorites
DELETE /api/v1/client/favorites/{id}

GET    /api/v1/client/saved-lists
POST   /api/v1/client/saved-lists
PATCH  /api/v1/client/saved-lists/{id}
DELETE /api/v1/client/saved-lists/{id}
POST   /api/v1/client/saved-lists/{id}/items
DELETE /api/v1/client/saved-lists/{id}/items/{itemId}
```

---

## 13. Store APIs

### Public Store APIs

```http
GET /api/v1/stores
GET /api/v1/stores/{id}
GET /api/v1/stores/{id}/products
GET /api/v1/stores/{id}/products/{productId}
GET /api/v1/stores/{id}/delivery-time-slots
```

### Store Owner APIs

```http
POST   /api/v1/stores
GET    /api/v1/store-owner/stores
GET    /api/v1/store-owner/stores/{id}
PATCH  /api/v1/store-owner/stores/{id}
PATCH  /api/v1/store-owner/stores/{id}/delivery-settings
GET    /api/v1/store-owner/stores/{id}/dashboard
```

### Store Staff APIs

```http
GET   /api/v1/store/staff/orders
GET   /api/v1/store/staff/orders/{id}
PATCH /api/v1/store/staff/orders/{id}/status
```

---

## 14. Product APIs

```http
GET    /api/v1/store-owner/stores/{storeId}/products
POST   /api/v1/store-owner/stores/{storeId}/products
GET    /api/v1/store-owner/stores/{storeId}/products/{productId}
PATCH  /api/v1/store-owner/stores/{storeId}/products/{productId}
DELETE /api/v1/store-owner/stores/{storeId}/products/{productId}
PATCH  /api/v1/store-owner/stores/{storeId}/products/{productId}/availability
POST   /api/v1/store-owner/stores/{storeId}/products/{productId}/duplicate
```

Bulk import later:

```http
POST /api/v1/store-owner/stores/{storeId}/products/bulk-import
```

---

## 15. Inventory APIs

```http
GET   /api/v1/store-owner/stores/{storeId}/inventory
GET   /api/v1/store-owner/stores/{storeId}/inventory/low-stock
GET   /api/v1/store-owner/stores/{storeId}/inventory/out-of-stock
PATCH /api/v1/store-owner/stores/{storeId}/inventory/{productId}
POST  /api/v1/store-owner/stores/{storeId}/inventory/{productId}/adjust
GET   /api/v1/store-owner/stores/{storeId}/inventory-movements
GET   /api/v1/store-owner/stores/{storeId}/inventory/alerts
```

### Adjust Inventory Body

Add stock:

```json
{
  "quantityChange": 20,
  "reason": "New stock received"
}
```

Remove stock:

```json
{
  "quantityChange": -5,
  "reason": "Damaged items"
}
```

---

## 16. Order APIs

### Client Order APIs

```http
POST /api/v1/client/orders
GET  /api/v1/client/orders
GET  /api/v1/client/orders/{id}
POST /api/v1/client/orders/{id}/cancel
GET  /api/v1/client/orders/{id}/receipt
GET  /api/v1/client/orders/{id}/timeline
GET  /api/v1/client/orders/{id}/eta
POST /api/v1/client/orders/{id}/reorder
```

### Create Order Body Example

```json
{
  "storeId": "store_123",
  "addressId": "address_456",
  "paymentMethod": "CASH_ON_DELIVERY",
  "notes": "Call before arriving",
  "needsChange": true,
  "cashAmountClientHas": 20.00,
  "items": [
    {
      "productId": "product_1",
      "quantity": 2
    },
    {
      "productId": "product_2",
      "quantity": 1
    }
  ]
}
```

### Store Order APIs

```http
GET  /api/v1/store-owner/stores/{storeId}/orders
GET  /api/v1/store-owner/stores/{storeId}/orders/{orderId}
POST /api/v1/store-owner/stores/{storeId}/orders/{orderId}/accept
POST /api/v1/store-owner/stores/{storeId}/orders/{orderId}/reject
POST /api/v1/store-owner/stores/{storeId}/orders/{orderId}/mark-preparing
POST /api/v1/store-owner/stores/{storeId}/orders/{orderId}/mark-ready
POST /api/v1/store-owner/stores/{storeId}/orders/{orderId}/request-delivery
POST /api/v1/store-owner/stores/{storeId}/orders/{orderId}/assign-store-driver
GET  /api/v1/store-owner/stores/{storeId}/orders/{orderId}/delivery-recommendation
```

---

## 17. Service Provider APIs

### Public APIs

```http
GET /api/v1/service-providers
GET /api/v1/service-providers/{id}
GET /api/v1/service-providers/{id}/services
GET /api/v1/service-providers/{id}/services/{serviceId}
GET /api/v1/service-providers/{id}/time-slots
```

### Provider Owner APIs

```http
POST  /api/v1/provider-owner/providers
GET   /api/v1/provider-owner/providers
GET   /api/v1/provider-owner/providers/{id}
PATCH /api/v1/provider-owner/providers/{id}
GET   /api/v1/provider-owner/providers/{id}/dashboard
```

---

## 18. Service APIs

```http
GET    /api/v1/provider-owner/providers/{providerId}/services
POST   /api/v1/provider-owner/providers/{providerId}/services
GET    /api/v1/provider-owner/providers/{providerId}/services/{serviceId}
PATCH  /api/v1/provider-owner/providers/{providerId}/services/{serviceId}
DELETE /api/v1/provider-owner/providers/{providerId}/services/{serviceId}
PATCH  /api/v1/provider-owner/providers/{providerId}/services/{serviceId}/availability
```

---

## 19. Service Request APIs

### Client APIs

```http
POST /api/v1/client/service-requests
GET  /api/v1/client/service-requests
GET  /api/v1/client/service-requests/{id}
POST /api/v1/client/service-requests/{id}/cancel
POST /api/v1/client/service-requests/{id}/accept-quote
POST /api/v1/client/service-requests/{id}/reject-quote
GET  /api/v1/client/service-requests/{id}/receipt
```

### Create Service Request Body

```json
{
  "providerId": "provider_123",
  "serviceId": "service_456",
  "addressId": "address_789",
  "description": "Need phone screen replacement",
  "preferredTime": "2026-05-02T10:00:00Z"
}
```

### Provider APIs

```http
GET  /api/v1/provider-owner/providers/{providerId}/service-requests
GET  /api/v1/provider-owner/providers/{providerId}/service-requests/{requestId}
POST /api/v1/provider-owner/providers/{providerId}/service-requests/{requestId}/accept
POST /api/v1/provider-owner/providers/{providerId}/service-requests/{requestId}/reject
POST /api/v1/provider-owner/providers/{providerId}/service-requests/{requestId}/send-quote
POST /api/v1/provider-owner/providers/{providerId}/service-requests/{requestId}/mark-in-progress
POST /api/v1/provider-owner/providers/{providerId}/service-requests/{requestId}/mark-ready
POST /api/v1/provider-owner/providers/{providerId}/service-requests/{requestId}/complete
POST /api/v1/provider-owner/providers/{providerId}/service-requests/{requestId}/request-delivery
```

---

## 20. Delivery APIs

### Driver APIs

```http
GET   /api/v1/driver/profile
PATCH /api/v1/driver/profile
POST  /api/v1/driver/availability
POST  /api/v1/driver/location
GET   /api/v1/driver/jobs/available
GET   /api/v1/driver/jobs/{id}
POST  /api/v1/driver/jobs/{id}/accept
POST  /api/v1/driver/jobs/{id}/reject
POST  /api/v1/driver/deliveries/{id}/arrived-pickup
POST  /api/v1/driver/deliveries/{id}/picked-up
POST  /api/v1/driver/deliveries/{id}/on-the-way
POST  /api/v1/driver/deliveries/{id}/arrived-destination
POST  /api/v1/driver/deliveries/{id}/delivered
POST  /api/v1/driver/deliveries/{id}/failed
POST  /api/v1/driver/deliveries/{id}/cash-collected
POST  /api/v1/driver/deliveries/{id}/report-issue
GET   /api/v1/driver/deliveries/issue-reasons
GET   /api/v1/driver/deliveries/history
GET   /api/v1/driver/earnings
GET   /api/v1/driver/performance
```

### Cash Collected Body

```json
{
  "collectedAmount": 12.50,
  "needsChange": false,
  "note": "Client paid exact amount"
}
```

### Delivery Issue Body

```json
{
  "reason": "CUSTOMER_NOT_ANSWERING",
  "note": "Called 3 times, no answer"
}
```

### Store/Provider Delivery APIs

```http
POST /api/v1/deliveries/request
GET  /api/v1/deliveries/{id}
GET  /api/v1/deliveries/{id}/tracking
GET  /api/v1/deliveries/{id}/timeline
GET  /api/v1/deliveries/{id}/eta
POST /api/v1/deliveries/{id}/cancel
```

### Custom Delivery APIs

```http
POST /api/v1/client/custom-deliveries
GET  /api/v1/client/custom-deliveries
GET  /api/v1/client/custom-deliveries/{id}
POST /api/v1/client/custom-deliveries/{id}/cancel
```

---

## 21. Receipt APIs

```http
GET /api/v1/client/receipts
GET /api/v1/client/receipts/{id}
GET /api/v1/store-owner/stores/{storeId}/receipts
GET /api/v1/provider-owner/providers/{providerId}/receipts
GET /api/v1/admin/receipts
GET /api/v1/admin/receipts/{id}
POST /api/v1/receipts/{id}/report-issue
```

Future:

```http
GET  /api/v1/receipts/{id}/download-pdf
POST /api/v1/receipts/{id}/send-email
```

---

## 22. Payment APIs

MVP method:

```text
Cash on delivery
```

APIs:

```http
GET  /api/v1/payments/{id}
POST /api/v1/payments/{id}/mark-collected
POST /api/v1/payments/{id}/mark-paid
GET  /api/v1/admin/payments
GET  /api/v1/driver/payments/collected-cash
GET  /api/v1/admin/cash-collections
GET  /api/v1/admin/drivers/{id}/cash-collections
```

Future online payments:

```http
POST /api/v1/payments/create-intent
POST /api/v1/payments/webhook
POST /api/v1/payments/refund
```

---

## 23. Contact and WhatsApp APIs

Contact options should be permission-based.

```http
GET /api/v1/orders/{id}/contact-options
GET /api/v1/service-requests/{id}/contact-options
GET /api/v1/deliveries/{id}/contact-options
```

Response example:

```json
{
  "canCallStore": true,
  "canCallDriver": true,
  "canWhatsappStore": true,
  "whatsappPrefilledMessage": "Hello, I am contacting you about Order #ORD-000928."
}
```

---

## 24. Concierge and Help Search APIs

### Urgent Requests

```http
POST /api/v1/client/urgent-requests
GET  /api/v1/client/urgent-requests
GET  /api/v1/admin/urgent-requests
```

### Concierge Requests

```http
POST /api/v1/client/concierge-requests
GET  /api/v1/client/concierge-requests
GET  /api/v1/client/concierge-requests/{id}
POST /api/v1/store-owner/concierge-requests/{id}/respond
POST /api/v1/provider-owner/concierge-requests/{id}/respond
GET  /api/v1/admin/concierge-requests
```

### Help Me Find It

```http
POST /api/v1/client/help-me-find-it
```

Body:

```json
{
  "text": "I need printer ink near me",
  "location": {
    "lat": 33.8938,
    "lng": 35.5018
  }
}
```

Response:

```json
{
  "suggestions": [
    {
      "type": "store",
      "title": "Stationery stores near you"
    },
    {
      "type": "custom_delivery",
      "title": "Ask a driver to pick it up"
    }
  ]
}
```

---

## 25. Notification APIs

```http
GET  /api/v1/notifications
POST /api/v1/notifications/{id}/read
POST /api/v1/notifications/read-all
```

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

---

## 26. Messaging APIs

For MVP, start with:

- Order notes.
- Call buttons.
- WhatsApp fallback.
- Support tickets.

Full in-app messaging later:

```http
GET  /api/v1/conversations
POST /api/v1/conversations
GET  /api/v1/conversations/{id}/messages
POST /api/v1/conversations/{id}/messages
```

Conversation types:

```text
CLIENT_STORE
CLIENT_DRIVER
STORE_DRIVER
CLIENT_PROVIDER
PROVIDER_DRIVER
SUPPORT
```

---

## 27. Ratings APIs

```http
POST /api/v1/client/ratings
GET  /api/v1/stores/{id}/ratings
GET  /api/v1/service-providers/{id}/ratings
GET  /api/v1/drivers/{id}/ratings
```

Rating targets:

```text
STORE
SERVICE_PROVIDER
DRIVER
ORDER
SERVICE_REQUEST
DELIVERY
```

---

## 28. Support APIs

User support:

```http
POST /api/v1/support/tickets
GET  /api/v1/support/tickets
GET  /api/v1/support/tickets/{id}
POST /api/v1/support/tickets/{id}/reply
POST /api/v1/support/tickets/{id}/close
```

Admin support:

```http
GET  /api/v1/admin/support/tickets
GET  /api/v1/admin/support/tickets/{id}
POST /api/v1/admin/support/tickets/{id}/assign
POST /api/v1/admin/support/tickets/{id}/reply
POST /api/v1/admin/support/tickets/{id}/close
```

---

## 29. Admin APIs

```http
GET /api/v1/admin/dashboard
GET /api/v1/admin/users
GET /api/v1/admin/stores
GET /api/v1/admin/providers
GET /api/v1/admin/drivers
GET /api/v1/admin/orders
GET /api/v1/admin/service-requests
GET /api/v1/admin/deliveries
GET /api/v1/admin/receipts
GET /api/v1/admin/payments
```

Approval actions:

```http
POST /api/v1/admin/stores/{id}/approve
POST /api/v1/admin/stores/{id}/reject
POST /api/v1/admin/providers/{id}/approve
POST /api/v1/admin/providers/{id}/reject
POST /api/v1/admin/drivers/{id}/approve
POST /api/v1/admin/drivers/{id}/reject
```

Management actions:

```http
PATCH /api/v1/admin/stores/{id}/status
PATCH /api/v1/admin/providers/{id}/status
PATCH /api/v1/admin/drivers/{id}/status
PATCH /api/v1/admin/users/{id}/status
```

Settings:

```http
GET    /api/v1/admin/categories
POST   /api/v1/admin/categories
PATCH  /api/v1/admin/categories/{id}
DELETE /api/v1/admin/categories/{id}

GET    /api/v1/admin/zones
POST   /api/v1/admin/zones
PATCH  /api/v1/admin/zones/{id}
DELETE /api/v1/admin/zones/{id}

GET    /api/v1/admin/fees
PATCH  /api/v1/admin/fees
```

Monitoring:

```http
GET /api/v1/admin/monitoring/delayed-orders
GET /api/v1/admin/monitoring/failed-deliveries
GET /api/v1/admin/monitoring/cash-unsettled
GET /api/v1/admin/monitoring/problem-stores
GET /api/v1/admin/monitoring/problem-drivers
```

Risk/quality later:

```http
GET  /api/v1/admin/risk-flags
GET  /api/v1/admin/risk-flags/{id}
POST /api/v1/admin/risk-flags/{id}/resolve

GET /api/v1/admin/quality-scores
GET /api/v1/admin/stores/{id}/quality-score
GET /api/v1/admin/providers/{id}/quality-score
GET /api/v1/admin/drivers/{id}/quality-score
```

---

## 30. Realtime Events

Use WebSockets later. MVP can start with polling.

### Client Events

```text
order.status.updated
delivery.driver.assigned
delivery.location.updated
delivery.status.updated
service_request.status.updated
quote.sent
payment.status.updated
```

### Store Events

```text
order.created
order.cancelled
delivery.driver.assigned
delivery.status.updated
payment.collected
inventory.low_stock
```

### Driver Events

```text
delivery.job.created
delivery.job.cancelled
delivery.assigned
delivery.details.updated
```

### Admin Events

```text
order.created
delivery.failed
support.ticket.created
payment.disputed
```

---

## 31. Validation Rules

### Order Validation

Before creating an order:

- Store must be active.
- Store must be open or accepting orders.
- Products must exist.
- Products must belong to selected store.
- Products must be available.
- Inventory must be enough.
- Delivery address must be valid.
- Backend must calculate total.

### Inventory Validation

- Quantity cannot go below zero unless admin override is allowed.
- Every stock change creates inventory movement.
- Product price changes must not affect old orders.

### Delivery Validation

- Driver must be approved.
- Driver must be available.
- Delivery job must not already be assigned.
- Driver can update only assigned delivery.

### Receipt Validation

- Receipt generated from backend values.
- Receipt number must be unique.
- Receipt keeps price and total snapshots.

---

## 32. Error Handling

Use clear error responses.

Example:

```json
{
  "success": false,
  "message": "Product is out of stock",
  "code": "PRODUCT_OUT_OF_STOCK",
  "errors": []
}
```

Common error codes:

```text
UNAUTHORIZED
FORBIDDEN
VALIDATION_ERROR
NOT_FOUND
PRODUCT_OUT_OF_STOCK
STORE_CLOSED
ORDER_ALREADY_ACCEPTED
DELIVERY_ALREADY_ASSIGNED
PAYMENT_ALREADY_COLLECTED
INSUFFICIENT_STOCK
INVALID_STATUS_TRANSITION
```

---

## 33. Audit Logs

Log important actions:

- User login.
- Store approval/rejection.
- Driver approval/rejection.
- Order accepted/rejected.
- Inventory adjusted.
- Delivery assigned.
- Payment marked collected.
- Admin status changes.
- Refunds later.
- Manual admin changes.

Suggested model:

```text
audit_logs
- id
- actor_user_id
- action
- target_type
- target_id
- old_value
- new_value
- ip_address
- user_agent
- created_at
```

---

## 34. MVP Feature List

### Must Have

#### Client

- Register/login.
- Browse stores.
- Browse products.
- Add to cart.
- Place order.
- Track order.
- View receipt.
- Stock status.
- Basic ETA.
- Order timeline.
- Call/WhatsApp fallback.
- Need-change option.

#### Store

- Store login.
- Product management.
- Basic inventory.
- Accept/reject orders.
- Mark preparing/ready.
- Request delivery.
- View receipts.

#### Delivery

- Driver login.
- Driver availability.
- View available jobs.
- Accept job.
- Update delivery status.
- Mark cash collected.

#### Admin

- View users.
- Approve stores.
- Approve drivers.
- View orders.
- View deliveries.
- View receipts.
- View cash collection.

#### Backend

- Authentication.
- Authorization.
- Orders.
- Inventory.
- Deliveries.
- Receipts.
- Cash payments.
- Notifications.

---

## 35. Feature Priority

### Must Add Soon

```text
Smart ETA and confidence
Stock certainty badges
Better order timeline UI
Call and WhatsApp fallback
COD improvements with need-change option
One-tap reorder
Scheduled delivery / time slots
Emergency categories
Driver trust profile
Better store pages
Better provider pages
Helpful empty states
```

### Strong V2 Features

```text
Request Anything / Concierge Mode
Help Me Find It intent-based search
Favorite stores and saved lists
Household accounts
Better driver exception flows
Missed sales insights for stores
Driver performance score
Better admin quality dashboards
```

### Later / Advanced Features

```text
Group orders
Full in-app chat
AI recommendations
Loyalty system
Online card payments
Wallet system
Route optimization
Advanced analytics
Proof of delivery media tools
Fraud scoring system
Barcode scanning
Advanced accounting
```

---

## 36. Development Order

### Step 1: Backend Foundation

- Project setup.
- Database setup.
- Authentication.
- Roles and permissions.
- User model.
- Audit logs.

### Step 2: Store Foundation

- Store model.
- Product model.
- Inventory model.
- Stock status badges.
- Store dashboard APIs.

### Step 3: Client Ordering

- Public store/product APIs.
- Cart logic.
- Create order API.
- Need-change COD option.
- Order status tracking.

### Step 4: Store Order Management

- Store receives order.
- Accept/reject order.
- Inventory transaction.
- Mark preparing/ready.

### Step 5: Order Timeline and Notifications

- Timeline endpoint.
- Basic ETA.
- Push notifications.

### Step 6: Delivery

- Driver model.
- Available jobs.
- Accept delivery.
- Status updates.
- Cash collection.
- Driver trust profile.

### Step 7: Receipts and Payments

- Generate receipts.
- Payment status.
- Cash collection records.
- Reorder endpoint.

### Step 8: Admin Dashboard

- User management.
- Store approval.
- Driver approval.
- View orders/deliveries/receipts.
- Cash monitoring.
- Delayed/failed delivery monitoring.

### Step 9: Service Provider Flow

- Provider profile.
- Services.
- Service requests.
- Quotes.
- Provider receipts.

### Step 10: V2 Problem-Solving Features

- Request Anything.
- Help Me Find It.
- Favorites.
- Scheduled deliveries.
- Missed sales insights.
- Better driver issue flows.

---

## 37. Manual MVP Test Scenario

The most important test:

```text
1. Admin approves a store.
2. Store adds products and inventory.
3. Client places order.
4. Store accepts order.
5. Inventory decreases.
6. Store requests delivery.
7. Driver accepts delivery.
8. Driver picks up order.
9. Driver delivers order.
10. Driver collects cash.
11. Receipt becomes paid.
12. Admin sees completed order.
```

If this works smoothly, the platform foundation is correct.

---

## 38. Final Functional Summary

The app must make these flows clear and reliable:

```text
Client → Store → Delivery → Client
Client → Service Provider → Delivery → Client
Client → Custom Delivery → Driver → Client
```

The strongest MVP foundation is:

```text
Client places order
  ↓
Store accepts order
  ↓
Inventory updates
  ↓
Delivery is assigned
  ↓
Driver delivers order
  ↓
Cash is collected
  ↓
Receipt is generated
  ↓
Admin can monitor everything
```
