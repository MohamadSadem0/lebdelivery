# Store-Type Flexible Architecture + Codex Implementation Prompt

## Project Context

The platform is a connected local commerce, services, and delivery ecosystem for Lebanon.

It has 4 main systems:

1. **Store System**
2. **Delivery System**
3. **Service Provider System**
4. **Client System**

It also needs a shared **Admin System** later to control approvals, monitoring, support, payments, receipts, issues, and quality.

The platform should support all store types in Lebanon, including restaurants, groceries, mini markets, supermarkets, pharmacies, clothing shops, electronics shops, bakeries, flower shops, butchers, phone shops, pet shops, cosmetics stores, stationery shops, hardware stores, and any custom store type.

The goal is to avoid building many disconnected apps. Instead, we should build one powerful store system that supports multiple store types through templates, configuration, flexible product attributes, different inventory modes, and store-type-specific rules.

---

# 1. Main Architecture Decision

## Use One Core Store System

Do **not** create a totally separate system for each store type at the beginning.

Bad early architecture:

```text
Grocery System
Restaurant System
Clothing System
Pharmacy System
Electronics System
Bakery System
...
```

This causes:

```text
duplicated authentication
duplicated authorization
duplicated products
duplicated inventory logic
duplicated orders
duplicated delivery logic
duplicated receipts
duplicated admin tools
duplicated reports
high maintenance cost
slower development
more bugs
```

Recommended architecture:

```text
One Core Store System
        +
Store-Type Templates
        +
Configurable Fields
        +
Different Inventory Modes
        +
Store-Type Rules
```

Final structure:

```text
Store System
  ├── Core Store Engine
  │     ├── Store profile
  │     ├── Staff
  │     ├── Products
  │     ├── Inventory
  │     ├── Orders
  │     ├── Receipts
  │     ├── Delivery settings
  │     ├── Reports
  │     └── Communication
  │
  ├── Store-Type Templates
  │     ├── Restaurant / Food
  │     ├── Grocery / Mini Market / Supermarket
  │     ├── Pharmacy
  │     ├── Clothing / Fashion
  │     ├── Electronics / Phone Shop
  │     ├── Bakery
  │     ├── Flower Shop
  │     ├── Butcher
  │     ├── Pet Shop
  │     ├── Cosmetics
  │     ├── Stationery
  │     ├── Hardware
  │     └── General Custom Store
  │
  └── Store-Type Rules
        ├── Product fields
        ├── Inventory mode
        ├── Order flow
        ├── Delivery options
        ├── Preparation rules
        ├── Availability rules
        └── Receipt format
```

---

# 2. Why This Architecture Is Best

Most stores share the same core needs:

```text
Store profile
Categories
Products/items
Prices
Images
Inventory or availability
Orders
Receipts
Delivery options
Staff
Reports
Customer communication
```

But each store type has special details.

Instead of building a new system for every type, the platform should use:

```text
shared core tables
store_type field
product_type field
inventory_mode field
configurable attributes
product variants
store-type feature flags
dynamic forms on frontend
special backend rules only when needed
```

This keeps the system flexible and scalable.

---

# 3. Store Types and Special Requirements

## 3.1 Grocery / Mini Market / Supermarket

Special needs:

```text
Brand
Package size
Unit type: piece, kg, gram, liter, pack
Expiry date, optional
Stock quantity
Low-stock alert
Barcode later
Bulk import later
Best sellers
Frequently unavailable items
```

Example products:

```text
Pepsi 330ml
Rice 1kg
Milk 1L
Bananas per kg
Tuna can
Water pack
```

Inventory mode:

```text
quantity-based
weight-based for some items
```

---

## 3.2 Restaurant / Food

Special needs:

```text
Menu items
Modifiers
Extras
Meal size
Spice level
Preparation time
Kitchen status
Item availability
Combo meals
Optional notes
```

Example products:

```text
Chicken sandwich
Burger meal
Pizza
Salad
Coffee
Dessert
```

Inventory mode:

```text
availability-based for MVP
ingredient-based later
```

Restaurant-specific features later:

```text
kitchen display
prep queue
table orders
advanced menu modifiers
branch management
```

---

## 3.3 Clothing / Fashion

Special needs:

```text
Size variants
Color variants
Gender
Material
Brand
Season
Stock per variant
Return/exchange notes later
```

Example products:

```text
Black T-shirt
Jeans
Shoes
Jacket
Dress
```

Inventory mode:

```text
variant-based
```

Example variant stock:

```text
Black T-shirt
- S: 5
- M: 10
- L: 4

White T-shirt
- S: 3
- M: 7
- L: 2
```

---

## 3.4 Pharmacy

Special needs:

```text
Medicine availability
Prescription handling later
Urgent requests
Baby items
Personal care
Alternative medicine suggestions later
Expiry dates
Controlled item rules later
```

Example products:

```text
Pain reliever
Baby formula
Vitamins
Skin care
First aid items
```

Inventory mode:

```text
quantity-based
expiry-aware later
```

Important note:

For regulated items, the platform should avoid unsafe medical claims and should support pharmacist/store confirmation when needed.

---

## 3.5 Electronics / Phone Shop

Special needs:

```text
Brand
Model
Warranty
Specifications
Storage/RAM variants
Serial number later
Accessories
Repair service link
```

Example products:

```text
Phone charger
Bluetooth headphones
Samsung phone
Laptop charger
Screen protector
Power bank
```

Inventory mode:

```text
quantity-based
variant-based for models/options
```

---

## 3.6 Bakery

Special needs:

```text
Daily availability
Fresh batches
Preorders
Preparation time
Limited quantity
Event/cake notes
```

Inventory mode:

```text
availability-based or quantity-based
```

---

## 3.7 Flower Shop

Special needs:

```text
Bouquet customization
Occasion type
Delivery time
Gift notes
Recipient information
Color preferences
```

Inventory mode:

```text
availability-based
custom order flow later
```

---

## 3.8 Butcher / Meat / Fresh Food

Special needs:

```text
Weight-based ordering
Freshness notes
Cut/preparation notes
Price per kg
Minimum quantity
```

Inventory mode:

```text
weight-based
```

---

## 3.9 Pet Shop

Special needs:

```text
Pet type
Food size
Brand
Age category
Accessory categories
```

Inventory mode:

```text
quantity-based
variant-based where needed
```

---

## 3.10 Stationery / Office / School Supplies

Special needs:

```text
Brand
Item code
School/office category
Pack size
Printer ink compatibility
```

Inventory mode:

```text
quantity-based
```

---

## 3.11 General Custom Store

This is very important.

Use it for stores that do not fit perfectly yet.

Special needs:

```text
Basic products
Basic categories
Basic inventory
Optional custom fields
Standard order flow
Standard delivery flow
```

Inventory mode:

```text
quantity-based or availability-based
```

---

# 4. Store Type Grouping for MVP

Do not try to perfectly model every store type in version 1.

Start with store type groups:

```text
1. Restaurant / Food
2. Grocery / Mini Market / Supermarket
3. Pharmacy
4. Clothing / Retail
5. Electronics / Phone Shop
6. General Custom Store
```

These groups cover most early business cases.

Later add:

```text
Bakery
Flower shop
Butcher
Pet shop
Cosmetics
Stationery
Hardware store
Furniture
Bookstore
```

---

# 5. Store Core Features

Every store type should have:

```text
Store profile
Store type
Store status
Opening hours
Store location
Contact info
Delivery settings
Product catalog
Product categories
Product images
Prices
Inventory or availability
Orders
Receipts
Staff management
Store drivers
Reports
Call/WhatsApp fallback
Customer communication
```

Store delivery options:

```text
Own drivers
Platform independent drivers
Both
Pickup only
```

---

# 6. Inventory Modes

The inventory system must support different inventory modes.

## 6.1 Quantity-Based Inventory

Used for:

```text
Grocery
Pharmacy
Electronics
Pet shop
Stationery
Cosmetics
Hardware
General retail
```

Example:

```text
Pepsi 330ml: 120 units
```

Fields:

```text
quantity
low_stock_threshold
reserved_quantity later
stock_status
```

---

## 6.2 Variant-Based Inventory

Used for:

```text
Clothing
Shoes
Electronics variants
Products with size/color/options
```

Example:

```text
T-Shirt
- Color: Black, Size: M, Stock: 10
- Color: Black, Size: L, Stock: 6
- Color: White, Size: M, Stock: 4
```

Fields:

```text
variant_id
attributes
sku
price_adjustment
stock_quantity
```

---

## 6.3 Availability-Based Inventory

Used for:

```text
Restaurants
Bakeries
Custom made products
Some service-like stores
```

Example:

```text
Chicken sandwich: available / unavailable
```

Fields:

```text
is_available
available_today
preparation_minutes
```

---

## 6.4 Weight / Unit-Based Inventory

Used for:

```text
Butcher
Vegetables
Fruits
Nuts
Cheese
Some groceries
```

Example:

```text
Bananas: sold by kg
Meat: sold by kg
Cheese: sold by gram
```

Fields:

```text
unit_type
unit_price
minimum_quantity
quantity_step
available_weight
```

---

# 7. Product Modeling Strategy

Do not create a different product table for every store type.

Use a shared product model with variants and flexible attributes.

## 7.1 Core Product Table

```text
products
- id
- store_id
- category_id
- name
- description
- base_price
- image_url
- product_type
- inventory_mode
- is_available
- stock_status
- preparation_minutes
- created_at
- updated_at
- deleted_at
```

## 7.2 Product Variants

```text
product_variants
- id
- product_id
- name
- sku
- price_adjustment
- final_price
- stock_quantity
- is_available
- attributes_json
- created_at
- updated_at
```

Example for clothing:

```json
{
  "size": "M",
  "color": "Black"
}
```

Example for electronics:

```json
{
  "storage": "256GB",
  "color": "Blue"
}
```

Example for restaurant:

```json
{
  "size": "Large"
}
```

## 7.3 Product Attributes

Two possible approaches:

### Option A: Key/Value Attributes

```text
product_attributes
- id
- product_id
- key
- value
- value_type
```

Example:

```text
brand = Nestle
package_size = 1L
warranty = 1 year
material = cotton
```

### Option B: PostgreSQL JSONB

```text
products.attributes_json
```

Example:

```json
{
  "brand": "Nestle",
  "package_size": "1L",
  "expiry_date": "2026-08-01"
}
```

## Recommended Approach

Use both:

```text
Core searchable fields as real columns
Flexible optional fields as JSONB or attributes
```

Real columns should include:

```text
store_id
category_id
price
is_available
inventory_mode
stock_status
created_at
updated_at
```

Flexible fields can include:

```text
brand
size
color
warranty
expiry_date
material
spice_level
package_size
occasion
pet_type
```

---

# 8. Store Type Configuration

Create a configuration system that defines which fields/features each store type supports.

## 8.1 StoreTypeConfig Model

```text
store_type_configs
- id
- store_type
- display_name
- enabled_features_json
- required_fields_json
- optional_fields_json
- inventory_modes_json
- order_flow_type
- default_inventory_mode
- created_at
- updated_at
```

Example restaurant config:

```json
{
  "store_type": "RESTAURANT",
  "enabled_features": [
    "menu_modifiers",
    "preparation_time",
    "kitchen_status",
    "item_availability",
    "extras"
  ],
  "required_fields": [
    "name",
    "base_price",
    "preparation_minutes"
  ],
  "optional_fields": [
    "spice_level",
    "meal_size",
    "extras"
  ],
  "default_inventory_mode": "AVAILABILITY"
}
```

Example grocery config:

```json
{
  "store_type": "GROCERY",
  "enabled_features": [
    "stock_quantity",
    "low_stock_alert",
    "expiry_date",
    "unit_pricing",
    "barcode_later"
  ],
  "required_fields": [
    "name",
    "base_price",
    "quantity"
  ],
  "optional_fields": [
    "brand",
    "package_size",
    "expiry_date"
  ],
  "default_inventory_mode": "QUANTITY"
}
```

---

# 9. Dynamic Forms

The frontend should not hardcode one product form for every store.

Instead:

```text
Business app asks backend for store type config
        ↓
Backend returns enabled fields/features
        ↓
Frontend renders the correct form
        ↓
Submitted data is validated by backend
```

Example:

```http
GET /api/v1/store-types/RESTAURANT/config
```

Response:

```json
{
  "store_type": "RESTAURANT",
  "required_fields": ["name", "base_price", "preparation_minutes"],
  "optional_fields": ["spice_level", "meal_size", "extras"],
  "default_inventory_mode": "AVAILABILITY"
}
```

Dynamic forms should support:

```text
text input
number input
price input
select
multi-select
boolean toggle
date
image upload
variant builder
modifier builder
weight/unit settings
```

---

# 10. Backend Module Structure

Recommended Spring Boot backend structure:

```text
backend/src/main/java/com/lebanonplatform/
  PlatformApplication.java

  common/
    config/
    exception/
    security/
    validation/
    audit/
    utils/
    pagination/
    response/

  modules/
    auth/
    users/
    roles/

    stores/
      core/
      types/
      config/
      rules/

    products/
      core/
      variants/
      attributes/

    inventory/
      core/
      movements/
      strategies/

    orders/
    drivers/
    deliveries/
    payments/
    receipts/
    serviceproviders/
    services/
    servicerequests/
    notifications/
    support/
    admin/
```

Alternative simpler structure for MVP:

```text
modules/
  stores/
  storetypes/
  products/
  productvariants/
  inventory/
  orders/
```

Inside each module:

```text
api/
application/
domain/
dto/
repository/
mapper/
exception/
```

Example:

```text
modules/storetypes/
  api/
    StoreTypeController.java
  application/
    StoreTypeConfigService.java
  domain/
    StoreType.java
    StoreTypeConfig.java
    InventoryMode.java
  dto/
    StoreTypeConfigResponse.java
  repository/
    StoreTypeConfigRepository.java
```

---

# 11. Backend Rules by Store Type

Use strategy classes for store-type-specific logic.

Example:

```text
StoreTypeRule
  validateProduct()
  validateInventory()
  calculateAvailability()
  getRequiredFields()
```

Implementations:

```text
RestaurantStoreRules
GroceryStoreRules
ClothingStoreRules
PharmacyStoreRules
ElectronicsStoreRules
CustomStoreRules
```

Example:

```text
RestaurantStoreRules:
- product can use modifiers
- inventory may be availability-based
- preparation_minutes is recommended
- variants are optional

ClothingStoreRules:
- variants are recommended
- size/color attributes are common
- stock can exist per variant

GroceryStoreRules:
- quantity is important
- low stock alert is important
- unit type and package size are useful
```

This keeps special logic organized without creating separate systems.

---

# 12. API Endpoints Needed for Store Types

## Public / Client APIs

```http
GET /api/v1/store-types
GET /api/v1/stores?storeType=GROCERY
GET /api/v1/stores/{storeId}
GET /api/v1/stores/{storeId}/products
GET /api/v1/stores/{storeId}/products/{productId}
```

## Store Owner APIs

```http
GET  /api/v1/store-owner/store-types
GET  /api/v1/store-owner/store-types/{storeType}/config

POST /api/v1/store-owner/stores
GET  /api/v1/store-owner/stores
GET  /api/v1/store-owner/stores/{storeId}
PATCH /api/v1/store-owner/stores/{storeId}

POST /api/v1/store-owner/stores/{storeId}/products
GET  /api/v1/store-owner/stores/{storeId}/products
GET  /api/v1/store-owner/stores/{storeId}/products/{productId}
PATCH /api/v1/store-owner/stores/{storeId}/products/{productId}
DELETE /api/v1/store-owner/stores/{storeId}/products/{productId}

POST /api/v1/store-owner/stores/{storeId}/products/{productId}/variants
PATCH /api/v1/store-owner/stores/{storeId}/products/{productId}/variants/{variantId}
DELETE /api/v1/store-owner/stores/{storeId}/products/{productId}/variants/{variantId}

GET  /api/v1/store-owner/stores/{storeId}/inventory
POST /api/v1/store-owner/stores/{storeId}/inventory/adjust
GET  /api/v1/store-owner/stores/{storeId}/inventory/movements
```

## Admin APIs

```http
GET  /api/v1/admin/store-types
POST /api/v1/admin/store-types
PATCH /api/v1/admin/store-types/{id}
GET  /api/v1/admin/store-types/{id}/config
PATCH /api/v1/admin/store-types/{id}/config
```

---

# 13. Performance Strategy for Many Store Types

Supporting many store types will not hurt performance if the data model is clean.

Performance problems come from:

```text
bad indexes
no pagination
returning too much nested data
searching huge tables without search engine
saving live driver location into PostgreSQL
duplicating systems by store type
uncontrolled JSON usage
bad inventory queries
no caching
```

Performance-safe strategy:

```text
shared product/order/inventory tables
store_type column indexed
inventory_mode column
category_id indexed
store_id indexed
pagination everywhere
summary DTOs for list pages
detail DTOs for detail pages
Redis cache for store type config/category data
search engine later for product/store search
PostgreSQL partitioning later for huge order/delivery/log tables
```

Recommended indexes:

```text
stores.store_type
stores.status
stores.category_id
products.store_id
products.category_id
products.product_type
products.inventory_mode
products.is_available
product_variants.product_id
inventory.store_id
inventory.product_id
orders.store_id
orders.status
orders.created_at
```

---

# 14. Security Rules for Store Types

Every store-type feature must follow the same security rules:

```text
User must be authenticated
User must have correct role
User must own or be staff of the store
User must be allowed to use this store type feature
Backend validates store type required fields
Backend validates price/inventory
Backend calculates totals
Backend logs sensitive changes
```

Important examples:

```text
Store A cannot edit Store B products
Store staff cannot access owner-only settings unless permission allows
Client cannot see internal inventory movements
Driver cannot edit product availability
Admin changes to store type config must be audit logged
```

---

# 15. MVP Store Type Implementation

For first implementation, create:

```text
StoreType enum:
- RESTAURANT
- GROCERY
- PHARMACY
- CLOTHING
- ELECTRONICS
- CUSTOM

InventoryMode enum:
- QUANTITY
- VARIANT
- AVAILABILITY
- WEIGHT
```

Create basic configs for each type.

Do not build all advanced workflows yet.

MVP should support:

```text
Create store with store type
Create product with product type and inventory mode
Add simple inventory quantity
Add variants for clothing/electronics
Set availability for restaurants
Show stock status to clients
Create orders from products
Update inventory when store accepts order
```

---

# 16. Future Expansion

After MVP, add:

```text
Restaurant modifiers/extras
Ingredient-based inventory
Grocery barcode scanning
Bulk product import
Expiry alerts
Pharmacy prescription flow
Electronics warranty/serial tracking
Flower bouquet customization
Butcher weight-preparation notes
Saved lists by store type
Missed-sales insights
Advanced search/filtering
```

---

# 17. Final Principle

The store system should be:

```text
one shared engine
many store type templates
configurable forms
flexible product attributes
multiple inventory modes
store-type-specific rules only where needed
```

Do not split into separate systems too early.

Only split a store type into a separate service/module later if it becomes very large and requires specialized performance or workflows.

---

# 18. Professional Codex Prompt

Copy the prompt below into Codex to start implementation.

```md
You are a senior full-stack architect and implementation agent.

We are building the Lebanon Local Commerce & Delivery Platform.

The platform has 4 main systems:
1. Store System
2. Delivery System
3. Service Provider System
4. Client System

All systems must be connected through one efficient backend infrastructure.

The main stack is:
- Backend: Java 21 + Spring Boot + Maven + Spring Security + Spring Data JPA + PostgreSQL + Flyway
- Mobile: React Native + Expo + TypeScript
- Client state/data: TanStack Query + Zustand
- Forms/validation: React Hook Form + Zod
- Secure token storage: Expo Secure Store
- Location later: Expo Location + React Native Maps
- Push notifications later: Firebase Cloud Messaging
- Cache/realtime later: Redis

Important architecture decision:
For the Store System, do NOT create a separate app/system for every store type.
Create one Core Store System that supports all store types through:
- store type templates
- configurable product fields
- product variants
- flexible product attributes
- different inventory modes
- store-type-specific validation/rules

The system must eventually support:
- restaurants
- groceries
- mini markets
- supermarkets
- pharmacies
- clothing stores
- electronics stores
- bakeries
- flower shops
- butchers
- pet shops
- cosmetics stores
- stationery stores
- hardware stores
- custom stores

For MVP, support these store type groups:
- RESTAURANT
- GROCERY
- PHARMACY
- CLOTHING
- ELECTRONICS
- CUSTOM

Inventory modes:
- QUANTITY
- VARIANT
- AVAILABILITY
- WEIGHT

Business direction:
This is not just a delivery app. It is a connected local commerce, service, inventory, receipt, and delivery coordination platform for Lebanon.

The platform should support:
- clients ordering from stores
- stores managing products, inventory, orders, receipts, and delivery options
- service providers managing service requests and quotes
- independent drivers and store-owned drivers
- cash on delivery
- receipts
- order/delivery status tracking
- role-based authorization
- entity-level authorization
- Lebanon-specific flows like COD, WhatsApp/call fallback, urgent categories, store-owned drivers, and unstable internet handling

For this first task:
Do NOT build the entire product.
Build the clean project foundation and the first backend/mobile skeleton with store-type architecture included.

Your goals:

1. Inspect the current repository.

2. If the repo is empty or incomplete, create this monorepo:

lebanon-platform/
  backend/
  apps/
    client-mobile/
    driver-mobile/
  packages/
    shared-types/
  docs/

3. Create or update root AGENTS.md with:
- Project overview
- 4 systems explanation
- Chosen stack
- Store-type architecture decision
- Folder structure
- Coding conventions
- Security rules
- Performance rules
- Testing rules
- What not to overbuild in MVP

4. Create root README.md explaining:
- Project purpose
- Architecture
- Apps
- Backend
- How to run locally
- MVP scope

5. Add docker-compose.yml for:
- PostgreSQL
- Redis

6. Backend foundation:
Create Spring Boot backend under backend/.

Use:
- Java 21
- Spring Boot
- Maven
- Spring Web
- Spring Security
- Spring Data JPA
- Validation
- PostgreSQL driver
- Flyway
- Spring Boot Actuator

Backend package base:
com.lebanonplatform

Required package structure:

com.lebanonplatform
  common
    config
    exception
    response
    security
    validation
    audit
    utils
    pagination
  modules
    auth
    users
    roles
    stores
    storetypes
    products
    productvariants
    inventory
    orders
    drivers
    deliveries
    payments
    receipts
    serviceproviders
    services
    servicerequests
    notifications
    support
    admin

Inside each module, use this structure when practical:
- api
- application
- domain
- dto/request
- dto/response
- repository
- mapper
- exception

7. Backend config:
Create:
- application.yml
- application-dev.yml
- application-prod.yml

Use environment variable placeholders for:
- database URL
- database username
- database password
- JWT secret placeholder
- Redis URL placeholder
- allowed origins placeholder

8. Backend common foundation:
Create:
- ApiResponse wrapper
- ErrorResponse
- GlobalExceptionHandler
- BaseEntity with UUID id, createdAt, updatedAt
- Placeholder SecurityConfig
- CORS config placeholder
- OpenAPI/Swagger config if dependency is included
- Health check through Actuator
- README for backend

9. Backend domain models:
Create JPA entity skeletons only. Do not overbuild business logic yet.

Models:
- User
- Role
- UserRole
- Store
- StoreTypeConfig
- Product
- ProductVariant
- ProductAttribute or attributesJson support
- Inventory
- InventoryMovement
- Order
- OrderItem
- Driver
- Delivery
- Payment
- Receipt
- ServiceProvider
- Service
- ServiceRequest
- Notification
- SupportTicket
- AuditLog

Important model rules:
- Use UUID primary keys.
- Use BaseEntity for id, createdAt, updatedAt.
- Use BigDecimal for money.
- Use enums for statuses and types.
- Do not expose entities directly through controllers.
- Prepare DTO folders.
- Store order item name/price snapshots.
- Store receipt snapshots.
- Use soft delete field where useful for important business records.
- Avoid too much business logic now.

10. Backend enums:
Create these enums:

StoreType:
- RESTAURANT
- GROCERY
- PHARMACY
- CLOTHING
- ELECTRONICS
- BAKERY
- FLOWER_SHOP
- BUTCHER
- PET_SHOP
- COSMETICS
- STATIONERY
- HARDWARE
- CUSTOM

InventoryMode:
- QUANTITY
- VARIANT
- AVAILABILITY
- WEIGHT

StoreStatus:
- PENDING_APPROVAL
- ACTIVE
- SUSPENDED
- REJECTED

DeliveryMode:
- OWN_DRIVER
- PLATFORM_DRIVER
- BOTH
- PICKUP_ONLY

ProductStatus or StockStatus:
- IN_STOCK
- LOW_STOCK
- ONLY_FEW_LEFT
- OUT_OF_STOCK
- USUALLY_AVAILABLE
- UNAVAILABLE

OrderStatus:
- PENDING
- ACCEPTED_BY_STORE
- REJECTED_BY_STORE
- PREPARING
- READY_FOR_PICKUP
- DRIVER_ASSIGNED
- PICKED_UP
- ON_THE_WAY
- DELIVERED
- COMPLETED
- CANCELLED
- ISSUE_REPORTED

DeliveryStatus:
- WAITING_FOR_DRIVER
- DRIVER_ASSIGNED
- ARRIVED_AT_PICKUP
- PICKED_UP
- ON_THE_WAY
- ARRIVED_AT_DESTINATION
- DELIVERED
- FAILED_DELIVERY
- CANCELLED
- ISSUE_REPORTED

PaymentStatus:
- UNPAID
- PENDING_COLLECTION
- COLLECTED
- PAID
- DISPUTED
- REFUNDED
- CANCELLED

DriverStatus:
- PENDING_APPROVAL
- ACTIVE
- SUSPENDED
- REJECTED

DriverType:
- INDEPENDENT
- STORE_DRIVER
- PROVIDER_DRIVER

ServiceRequestStatus:
- PENDING
- ACCEPTED
- REJECTED
- WAITING_FOR_QUOTE
- QUOTE_SENT
- QUOTE_ACCEPTED
- IN_PROGRESS
- READY
- COMPLETED
- CANCELLED
- ISSUE_REPORTED

PricingType:
- FIXED
- STARTING_FROM
- HOURLY
- AFTER_INSPECTION
- CUSTOM_QUOTE

11. Store-type architecture implementation:
Create storetypes module with:
- StoreTypeConfig entity
- StoreTypeConfigRepository
- StoreTypeConfigService
- StoreTypeController

Add endpoint:
GET /api/v1/store-types

Add endpoint:
GET /api/v1/store-types/{storeType}/config

Seed basic configs using Flyway or a startup data initializer for:
- RESTAURANT
- GROCERY
- PHARMACY
- CLOTHING
- ELECTRONICS
- CUSTOM

Each config should define:
- enabled features
- required fields
- optional fields
- default inventory mode
- supported inventory modes

Example:
Restaurant config:
- default inventory mode: AVAILABILITY
- features: preparation_time, menu_modifiers_later, item_availability
- required fields: name, base_price
- optional fields: preparation_minutes, spice_level, meal_size

Grocery config:
- default inventory mode: QUANTITY
- features: stock_quantity, low_stock_alert, unit_pricing, expiry_date_later
- required fields: name, base_price, quantity
- optional fields: brand, package_size, unit_type, expiry_date

Clothing config:
- default inventory mode: VARIANT
- features: variants, size, color, stock_per_variant
- required fields: name, base_price
- optional fields: brand, material, gender, size, color

12. Product model:
Product should include:
- store
- category optional
- name
- description
- basePrice
- imageUrl
- storeType or productType if useful
- inventoryMode
- isAvailable
- stockStatus
- preparationMinutes optional
- attributesJson optional
- deletedAt optional

ProductVariant should include:
- product
- name
- sku
- priceAdjustment
- finalPrice
- stockQuantity
- isAvailable
- attributesJson

13. Inventory model:
Inventory should support:
- store
- product
- productVariant optional
- inventoryMode
- quantity
- lowStockThreshold
- reservedQuantity optional
- unitType optional
- updatedAt

InventoryMovement should include:
- store
- product
- productVariant optional
- movementType
- quantityChange
- previousQuantity
- newQuantity
- reason
- referenceType
- referenceId
- createdByUserId

14. Flyway:
Create initial migration files for main tables if practical.
At minimum:
- users
- roles
- user_roles
- stores
- store_type_configs
- products
- product_variants
- inventory
- inventory_movements
- orders
- order_items
- drivers
- deliveries
- payments
- receipts

Add indexes for obvious fields:
- stores.store_type
- stores.status
- products.store_id
- products.inventory_mode
- products.stock_status
- inventory.store_id
- inventory.product_id
- orders.store_id
- orders.status
- deliveries.driver_id
- deliveries.status

15. Backend placeholder controllers:
Create simple placeholder controllers or endpoints for:
- auth health placeholder
- store types list/config
- stores placeholder
- products placeholder
- inventory placeholder
- orders placeholder
- deliveries placeholder

Do not build complete business logic yet.

16. Mobile foundation:
Create two Expo React Native apps:
- apps/client-mobile
- apps/driver-mobile

Use:
- TypeScript
- Expo
- React Navigation
- TanStack Query
- Zustand
- React Hook Form
- Zod
- Expo Secure Store
- Expo Location
- Axios or clean fetch wrapper

17. Client mobile folder structure:

src/
  app/
    providers.tsx
    navigation/
  components/
    ui/
    forms/
    cards/
    feedback/
    layout/
  features/
    auth/
    home/
    stores/
    store-types/
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
    secureStorage.ts
    constants.ts
    formatters.ts
  hooks/
  types/
  theme/

18. Driver mobile folder structure:

src/
  app/
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
    constants.ts
    formatters.ts
  hooks/
  types/
  theme/

19. Mobile requirements:
Set up:
- navigation skeleton
- app providers
- API client placeholder
- TanStack Query provider
- Zustand auth store placeholder
- secure token storage helper
- theme folder
- constants

Create placeholder screens:

Client:
- LoginScreen
- HomeScreen
- StoreTypesScreen
- StoresScreen
- StoreDetailsScreen
- ProductDetailsScreen
- CartScreen
- OrdersScreen
- OrderTrackingScreen
- ProfileScreen

Driver:
- LoginScreen
- DriverHomeScreen
- AvailableJobsScreen
- JobDetailsScreen
- ActiveDeliveryScreen
- EarningsScreen
- ProfileScreen

20. Shared types:
Create packages/shared-types with TypeScript types:
- User
- Store
- StoreType
- StoreTypeConfig
- Product
- ProductVariant
- Inventory
- InventoryMode
- StockStatus
- Order
- OrderStatus
- Driver
- Delivery
- DeliveryStatus
- Payment
- PaymentStatus
- Receipt
- ServiceProvider
- ServiceRequest

These types should match backend DTO ideas, not internal JPA entities.

21. Security rules:
- Never hardcode secrets.
- Never expose password hashes.
- Do not store tokens in plain AsyncStorage.
- Backend must be source of truth for prices, totals, delivery fees, inventory, payment status, and permissions.
- Frontend permission checks are only for UX.
- Every future endpoint must check role and entity ownership.
- Store owner/staff can access only their own store.
- Client can access only their own orders.
- Driver can update only assigned deliveries.
- Admin changes must be audit logged.

22. Performance rules:
- Use pagination for future list APIs.
- Keep list DTOs small.
- Do not return deeply nested objects by default.
- Cache store type configs later.
- Use Redis later for driver live location.
- Do not write every driver location update to PostgreSQL.
- Use database indexes for query paths.
- Do not split into microservices now.

23. What NOT to implement now:
- Full payment gateway
- Full chat
- AI features
- microservices
- Kubernetes
- advanced route optimization
- full restaurant modifiers
- full pharmacy prescription workflow
- full inventory analytics
- full admin dashboard
- real production auth flows unless foundation is ready

24. Output expected:
After implementing, summarize:
- What files/folders were created
- How to run backend
- How to run client mobile app
- How to run driver mobile app
- What store-type architecture was added
- What was intentionally left as placeholder
- Any assumptions or errors

Focus only on clean project foundation and flexible store-type architecture.
```

---

# 19. Next Codex Prompt After This One

After Codex finishes the foundation, the next prompt should be for:

```text
Authentication + Role-Based Authorization + Entity-Level Authorization
```

That should include:

```text
register/login
JWT access token
refresh token
roles
user_roles
active role selection
store ownership checks
driver ownership checks
client order ownership checks
permission guards
security tests
```

Do not build ordering before authorization is clean.
