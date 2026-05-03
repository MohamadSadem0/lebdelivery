# Lebanon Local Commerce & Delivery Platform - Full App Implementation Plan

## 1. Product Goal

Build one connected Lebanon-focused platform for:

- Local commerce from stores and restaurants.
- Inventory, products, receipts, and store operations.
- Service providers, quotes, and service request fulfillment.
- Delivery coordination with independent and store-owned drivers.
- Cash on delivery and cash collection monitoring.
- Client ordering, tracking, receipts, support, and repeat usage.
- Admin approval, monitoring, support, issue handling, and platform controls.

The platform should start as a modular monolith and grow only after the core flow is stable.

## 2. Main Systems

### 2.1 Client System

Main users:

- People ordering products.
- People requesting services.
- People requesting custom delivery later.

Apps:

- Client mobile app: Expo React Native.
- Client website: React/Vite web app for browsing, ordering, tracking, receipts, and support.

Recommended first priority:

1. Client mobile app.
2. Client website after mobile checkout and tracking are stable.

Reason:

Lebanon delivery and commerce usage will likely be mobile-first, but a website helps discovery, SEO, and desktop ordering.

### 2.2 Store System

Main users:

- Store owners.
- Store staff.
- Restaurant operators.
- Grocery/pharmacy/electronics/clothing/etc. operators.

Apps:

- Store web dashboard: React/Vite.
- Store desktop app: Tauri or Electron wrapper around the web dashboard.
- Store mobile app: Expo React Native or a shared business mobile app.

Recommended first priority:

1. Store web dashboard.
2. Store desktop app as a wrapper after dashboard is stable.
3. Store mobile app for lightweight owner actions and notifications.

Reason:

Store operations need bigger screens for products, inventory, and order queues. A desktop app can reuse the web dashboard and add native conveniences later, instead of building a separate product too early.

### 2.3 Delivery System

Main users:

- Independent drivers.
- Store drivers later.
- Provider drivers later.
- Delivery operations/admin teams.

Apps:

- Driver mobile app: Expo React Native.
- Delivery operations website: React/Vite, likely part of admin dashboard first.
- Optional driver web portal later for earnings/history/profile.

Recommended first priority:

1. Driver mobile app.
2. Admin delivery monitoring web pages.
3. Driver web portal later, only if drivers need desktop access.

Reason:

Drivers need mobile GPS, job acceptance, navigation, cash collection, and status actions. The website is more useful for operations and admin monitoring than for drivers themselves.

### 2.4 Service Provider System

Main users:

- Service provider owners.
- Provider staff.
- Clients requesting services.

Apps:

- Provider web dashboard: can share the business dashboard with stores.
- Provider desktop app: same wrapper strategy as store dashboard.
- Provider mobile app later, or provider mode inside a shared business mobile app.

Recommended first priority:

1. Provider mode inside `apps/business-dashboard`.
2. Client mobile service request flow.
3. Provider mobile app only after real provider workflows prove they need it.

Reason:

Provider workflows overlap with store workflows: profile, services, incoming requests, quotes, status updates, receipts, support.

### 2.5 Admin System

Main users:

- Platform admins.
- Support agents.
- Operations managers.
- Finance/cash settlement staff.

Apps:

- Admin web dashboard: React/Vite.
- Optional admin desktop wrapper later.

Recommended first priority:

1. Admin web dashboard.
2. Desktop wrapper only if daily operations need a native installed tool.

Reason:

Admin work is web-first and needs tables, filtering, approvals, monitoring, reports, and audit logs.

## 3. Recommended Final App Structure

```text
lebanon-platform/
  backend/
  apps/
    client-mobile/
    client-web/
    driver-mobile/
    delivery-ops-web/
    business-dashboard/
    business-mobile/
    business-desktop/
    admin-dashboard/
    admin-desktop/
  packages/
    shared-types/
    ui/
    api-client/
    config/
  docs/
```

Do not create all apps immediately. Add them in phases.

Recommended MVP apps:

```text
apps/client-mobile
apps/driver-mobile
apps/business-dashboard
apps/admin-dashboard
packages/shared-types
```

Recommended V1 apps after MVP works:

```text
apps/client-web
apps/business-mobile
apps/business-desktop
```

Recommended later:

```text
apps/delivery-ops-web
apps/admin-desktop
```

## 4. Technical Stack

### 4.1 Backend

- Java 21.
- Spring Boot.
- Spring Security.
- Spring Data JPA.
- PostgreSQL.
- Flyway.
- Redis.
- Actuator.
- Springdoc OpenAPI.

Architecture:

- Modular monolith first.
- One backend API.
- Modules under `com.lebanonplatform.modules`.
- Common infrastructure under `com.lebanonplatform.common`.
- No microservices until traffic, team size, and deployment needs justify it.

### 4.2 Mobile

- Expo React Native.
- TypeScript.
- React Navigation.
- TanStack Query.
- Zustand.
- React Hook Form.
- Zod.
- Expo Secure Store.
- Expo Location.
- React Native Maps later.

### 4.3 Web Dashboards and Websites

- React.
- Vite.
- TypeScript.
- TanStack Query.
- Zustand.
- React Hook Form.
- Zod.
- Tailwind CSS later if desired.
- shadcn/ui and Radix later for production-grade dashboards.
- TanStack Table for admin/store tables.
- Recharts for admin/business analytics.

### 4.4 Desktop

Recommended:

- Tauri first choice if the team is comfortable with it.
- Electron second choice if faster ecosystem support is more important.

Rule:

- Desktop apps should wrap/reuse the web dashboard code.
- Do not build a separate desktop product from scratch.

## 5. Current Repo State

Already started:

- Backend modular monolith.
- Client mobile app.
- Driver mobile app.
- Business dashboard.
- Admin dashboard.
- Shared types.
- Auth, roles, active roles.
- Store onboarding, product, inventory.
- Orders, COD, delivery request, driver acceptance, delivery status.
- Receipts.
- Admin monitoring and approvals.
- Service provider MVP.
- In-app notifications.

Still not complete:

- Production-grade UI.
- Client website.
- Business mobile app.
- Business desktop wrapper.
- Admin production UI polish.
- Maps/live location.
- Push notifications.
- Support tickets.
- Ratings/favorites.
- Scheduling.
- Custom delivery.
- File uploads.
- Production deployment.

## 6. Phase Roadmap

## Phase 0 - Stabilize Foundation

Goals:

- Keep local Docker setup reliable.
- Keep backend tests and frontend typechecks green.
- Keep OpenAPI available.
- Keep README accurate.

Tasks:

- Confirm Docker Compose starts PostgreSQL, Redis, and backend.
- Add CI checks for backend, mobile apps, dashboards, and shared packages.
- Add request IDs and structured errors.
- Keep `.env.example` files complete.
- Avoid secrets in source control.

Acceptance:

- New developer can clone, copy env examples, start services, run backend, and typecheck all apps.

## Phase 1 - Auth, Roles, Permissions

Goals:

- Shared auth across all systems.
- Users can hold multiple roles.
- Active role selection works.
- Entity-level authorization is enforced.

Tasks:

- Harden register/login/refresh/logout.
- Add profile update.
- Enforce ownership for stores, providers, orders, deliveries, receipts, and support tickets.
- Add rate limits for auth endpoints using Redis.
- Add integration tests for BOLA cases.

Acceptance:

- Client cannot access another client's order.
- Store owner cannot access another store.
- Provider owner cannot access another provider.
- Driver cannot update another driver's delivery.

## Phase 2 - Store System MVP

Goals:

- Stores can onboard and operate products/inventory/orders.

Tasks:

- Store profile.
- Store type config.
- Product categories.
- Products, variants, attributes.
- Inventory rows and movements.
- Low-stock/out-of-stock views.
- Store order queue.
- Order accept/reject/preparing/ready.
- Request platform delivery.

Apps:

- Business dashboard web first.
- Store desktop wrapper later.
- Business mobile later.

Acceptance:

- Store can create products, stock inventory, receive orders, accept orders, and request delivery.

## Phase 3 - Client Ordering MVP

Goals:

- Client can browse, order, pay COD, track, and view receipts.

Tasks:

- Home.
- Stores.
- Products.
- Cart.
- Checkout.
- COD fields.
- Orders.
- Tracking.
- Receipts.
- Notifications.
- Saved addresses.

Apps:

- Client mobile first.
- Client website after mobile order flow is stable.

Acceptance:

- Client places an order, store accepts it, delivery completes, cash is collected, receipt is visible.

## Phase 4 - Delivery MVP

Goals:

- Drivers can accept jobs and complete deliveries.

Tasks:

- Driver profile.
- Availability.
- Available jobs.
- Accept job.
- Active delivery.
- Status changes.
- Cash collection.
- History.
- Basic earnings.
- Failed delivery reason.

Apps:

- Driver mobile first.
- Delivery operations web inside admin dashboard.
- Driver website optional later.

Acceptance:

- Driver accepts delivery, updates status, collects COD, and the platform records payment/receipt state.

## Phase 5 - Receipts and Payments

Goals:

- COD receipts and cash tracking are reliable.

Tasks:

- Immutable receipt snapshots.
- Client receipts.
- Store receipts.
- Admin receipts.
- Cash collection records.
- Mismatch flag.
- Settlement views.

Acceptance:

- Completed COD order produces receipt and visible cash collection record.

## Phase 6 - Business Dashboard V1

Goals:

- Stores and service providers can operate from the web dashboard.

Store pages:

- My Stores.
- Products.
- Inventory.
- Orders.
- Deliveries.
- Receipts.
- Notifications.
- Settings.

Provider pages:

- My Providers.
- Services.
- Service Requests.
- Quotes.
- Receipts.
- Notifications.
- Settings.

Acceptance:

- A business can manage store and provider work from one dashboard.

## Phase 7 - Business Mobile App

Goals:

- Owners/staff can manage urgent tasks from mobile.

Recommended scope:

- Login and role selection.
- Notifications.
- Order queue.
- Accept/reject orders.
- Mark preparing/ready.
- Service requests.
- Send quotes.
- Basic inventory alerts.

Do not include:

- Full product catalog editing at first.
- Full reports at first.

Acceptance:

- Store/provider owner can handle urgent actions without desktop.

## Phase 8 - Business Desktop App

Goals:

- Give stores a reliable installed app for daily work.

Recommended approach:

- Wrap `business-dashboard` using Tauri.
- Reuse authentication, API client, UI, routes, and state.
- Add optional native features later:
  - receipt printing;
  - local notification badges;
  - auto-launch;
  - device-specific settings.

Acceptance:

- Desktop app behaves like the web dashboard with minimal duplicated code.

## Phase 9 - Admin Dashboard V1

Goals:

- Admin can approve actors and monitor platform health.

Pages:

- Overview.
- Users.
- Stores.
- Providers.
- Drivers.
- Orders.
- Deliveries.
- Receipts.
- Payments/cash.
- Support.
- Audit logs.
- Settings.

Actions:

- Approve/reject/suspend stores.
- Approve/reject/suspend providers.
- Approve/reject/suspend drivers.
- Activate/suspend users.
- Inspect failed deliveries.
- Inspect cash mismatches.

Acceptance:

- Admin can operate the full MVP flow and investigate problems.

## Phase 10 - Service Provider System V1

Goals:

- Service providers can manage requests and quotes.

Tasks:

- Provider profile.
- Services catalog.
- Client service request form.
- Provider accept/reject.
- Provider quote.
- Client accept quote.
- Provider mark in progress/ready/completed.
- Provider receipts.
- Optional provider delivery request.

Acceptance:

- Client requests service, provider quotes, client accepts, provider completes, receipt is generated.

## Phase 11 - Notifications

Goals:

- Important state changes reach the correct user.

Tasks:

- In-app notification records.
- Unread count.
- Mark read.
- FCM token registration.
- Push notifications.
- Notification preferences.

Events:

- Order accepted/rejected.
- Order preparing/ready.
- Driver assigned.
- Delivery status changes.
- Cash collected.
- Receipt generated.
- Quote sent.
- Service request status changes.
- Support updates.

Acceptance:

- Users see in-app notifications and later receive push notifications for critical state changes.

## Phase 12 - Maps and Live Tracking

Goals:

- Driver and client can track deliveries.

Tasks:

- Expo Location in driver mobile.
- React Native Maps in mobile apps.
- Google Maps provider first.
- Store latest driver location in Redis.
- Do not write every GPS update to PostgreSQL.
- Client can view assigned driver's latest location.
- Admin can inspect active delivery position.

Acceptance:

- During active delivery, client can track current driver position with polling.

## Phase 13 - Support and Issues

Goals:

- Problems can be reported, linked, and resolved.

Tasks:

- Support ticket model.
- Client support tickets.
- Driver issue reporting.
- Store issue reporting.
- Admin/support replies.
- Issue reasons.
- Ticket status.
- Audit logs for sensitive actions.

Acceptance:

- Failed delivery, payment problem, or missing item can be reported and managed by support.

## Phase 14 - Ratings, Favorites, and Retention

Goals:

- Clients can repeat common actions and rate completed experiences.

Tasks:

- Store ratings.
- Provider ratings.
- Driver ratings.
- Favorite stores.
- Favorite products.
- Favorite providers.
- Reorder.
- Saved lists.

Acceptance:

- Client can reorder, favorite, and rate completed orders/services.

## Phase 15 - Scheduling and Urgent Categories

Goals:

- Support Lebanon-specific demand patterns.

Tasks:

- Scheduled orders.
- Scheduled delivery.
- Store opening hours logic.
- Provider availability/time slots.
- Urgent pharmacy.
- Baby items.
- Water/gas.
- Documents.
- Laundry/repair pickup.

Acceptance:

- Client can choose now/scheduled and urgent categories route to the right product/service/custom flow.

## Phase 16 - Custom Delivery

Goals:

- Client can send packages without store/provider order.

Tasks:

- Pickup address.
- Dropoff address.
- Item description.
- Notes.
- Delivery fee.
- Driver job type.
- Delivery tracking.
- Custom delivery receipt.

Acceptance:

- Client creates package delivery, driver accepts, delivery completes, receipt is generated.

## Phase 17 - Files and Images

Goals:

- Platform supports media and documents safely.

Tasks:

- S3-compatible storage.
- Store logo.
- Product images.
- Provider images.
- Driver document placeholders.
- Support attachments.
- Proof of delivery later.
- File type and size validation.

Acceptance:

- Files upload through backend and render in apps without exposing storage secrets.

## Phase 18 - Client Website

Goals:

- Clients can browse and order from desktop/mobile web.

Recommended scope:

- Browse stores.
- Browse providers.
- Product/service details.
- Login.
- Cart.
- Checkout.
- Tracking.
- Receipts.
- Support.

Recommended implementation:

- React/Vite first.
- Consider Next.js later only if SEO/content pages become important.

Acceptance:

- A client can complete the same order flow from browser.

## Phase 19 - Delivery Operations Website

Goals:

- Operations team can monitor active deliveries.

Recommended scope:

- Active deliveries.
- Failed deliveries.
- Unassigned jobs.
- Driver status.
- Cash collection issues.
- Map view later.

Recommended implementation:

- Start as admin dashboard pages.
- Split into `apps/delivery-ops-web` only if operations team grows.

Acceptance:

- Operations can monitor and troubleshoot live delivery flow.

## Phase 20 - Production Hardening

Goals:

- Make the platform reliable enough for real stores/drivers.

Tasks:

- Staging environment.
- Production environment.
- Docker backend image.
- Managed PostgreSQL.
- Managed Redis.
- Backups.
- Migration procedure.
- Sentry.
- Structured logs.
- Request IDs.
- Metrics.
- Rate limits.
- JWT secret validation.
- CORS locked to real domains.
- Audit log review.
- EAS builds for mobile apps.
- Dashboard deployment pipeline.

Acceptance:

- Staging can run the complete MVP scenario before onboarding real users.

## 7. Recommended Build Order From Here

1. Finish support tickets and issue reporting.
2. Add ratings/favorites/reorder.
3. Add saved addresses and client website.
4. Add maps and Redis live driver location.
5. Add business mobile app for urgent store/provider actions.
6. Add business desktop wrapper after web dashboard stabilizes.
7. Add FCM push notifications.
8. Add custom delivery.
9. Add files/images storage.
10. Harden production deployment.

## 8. App Surface Recommendations

### Recommendation 1 - Do not build too many separate apps too early

Build fewer strong apps first:

- Client mobile.
- Driver mobile.
- Business dashboard.
- Admin dashboard.

Then add:

- Client web.
- Business mobile.
- Business desktop.

Reason:

Too many apps too early means duplicated auth, UI, state, testing, and release work.

### Recommendation 2 - Use one business dashboard for stores and providers

Do not create a separate store dashboard and provider dashboard at first.

Use:

- One business dashboard.
- Role/provider/store selection.
- Separate sections inside the same app.

Reason:

Many business users may operate both a store and service provider account.

### Recommendation 3 - Desktop should reuse web dashboard code

Use Tauri or Electron as a wrapper around the business dashboard.

Do not rewrite desktop screens.

Reason:

Desktop value is installation, printing, native notifications, and stable window behavior, not a separate UI.

### Recommendation 4 - Delivery website should be operations-focused

Drivers need mobile first.

The delivery website should be for:

- Admins.
- Dispatchers.
- Operations staff.
- Monitoring.
- Cash and issue resolution.

### Recommendation 5 - Client website should come after mobile checkout is solid

The client website should reuse:

- Shared types.
- API client package.
- UI package where possible.
- Checkout validation.

Do not build client website before the client mobile flow is reliable.

### Recommendation 6 - Add shared packages before adding more apps

Before adding many apps, create:

```text
packages/api-client
packages/ui
packages/config
```

Reason:

This prevents duplicated API calls, formatting, validation schemas, and UI patterns across web, desktop, and mobile surfaces.

## 9. Testing Strategy

Backend:

- Unit tests for status transitions.
- Integration tests for auth.
- Integration tests for entity ownership.
- Integration tests for order/inventory.
- Integration tests for delivery acceptance.
- Integration tests for cash collection.
- Integration tests for service requests.

Frontend:

- Typecheck all apps.
- Test Zustand stores.
- Test validation schemas.
- Test key page actions.
- Add E2E later for browser dashboards.

Critical security tests:

- Client cannot see another client's orders.
- Store owner cannot manage another store.
- Provider owner cannot manage another provider.
- Driver cannot update another driver's delivery.
- Admin-only routes reject non-admin users.

## 10. MVP Launch Criteria

The MVP is launchable only when:

- Admin can approve stores and drivers.
- Store can add products and inventory.
- Client can place an order.
- Store can accept and prepare the order.
- Store can request delivery.
- Driver can accept and complete delivery.
- Driver can collect COD.
- Receipt is generated.
- Client, store, driver, and admin can see correct final state.
- Critical notifications appear.
- Entity-level permissions are tested.

## 11. What To Avoid Until Later

Avoid:

- Microservices.
- Kubernetes.
- Online payment gateway.
- Wallet.
- Full chat.
- AI features.
- Advanced route optimization.
- Complex realtime infrastructure.
- Multi-store cart.
- Marketplace ads.
- Loyalty system.

Add those only after the core marketplace loop works with real users.
