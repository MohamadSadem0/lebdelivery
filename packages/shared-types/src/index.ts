export type UUID = string;
export type Money = string;

export type UserStatus = 'ACTIVE' | 'SUSPENDED' | 'INACTIVE';

export type RoleName =
  | 'ADMIN'
  | 'SUPPORT_AGENT'
  | 'CLIENT'
  | 'STORE_OWNER'
  | 'STORE_STAFF'
  | 'STORE_DRIVER'
  | 'PROVIDER_OWNER'
  | 'PROVIDER_STAFF'
  | 'INDEPENDENT_DRIVER';

export type EntityType = 'PLATFORM' | 'CLIENT' | 'STORE' | 'SERVICE_PROVIDER' | 'DRIVER';

export type OrderStatus =
  | 'PENDING'
  | 'ACCEPTED_BY_STORE'
  | 'REJECTED_BY_STORE'
  | 'PREPARING'
  | 'READY_FOR_PICKUP'
  | 'DRIVER_ASSIGNED'
  | 'PICKED_UP'
  | 'ON_THE_WAY'
  | 'DELIVERED'
  | 'COMPLETED'
  | 'CANCELLED'
  | 'ISSUE_REPORTED';

export type DeliveryStatus =
  | 'WAITING_FOR_DRIVER'
  | 'DRIVER_ASSIGNED'
  | 'ARRIVED_AT_PICKUP'
  | 'PICKED_UP'
  | 'ON_THE_WAY'
  | 'ARRIVED_AT_DESTINATION'
  | 'DELIVERED'
  | 'FAILED_DELIVERY'
  | 'CANCELLED'
  | 'ISSUE_REPORTED';

export type PaymentStatus =
  | 'UNPAID'
  | 'PENDING_COLLECTION'
  | 'COLLECTED'
  | 'PAID'
  | 'DISPUTED'
  | 'REFUNDED'
  | 'CANCELLED';

export type StoreTypeCode =
  | 'RESTAURANT'
  | 'GROCERY'
  | 'PHARMACY'
  | 'CLOTHING'
  | 'ELECTRONICS'
  | 'BAKERY'
  | 'FLOWER_SHOP'
  | 'BUTCHER'
  | 'PET_SHOP'
  | 'COSMETICS'
  | 'STATIONERY'
  | 'HARDWARE'
  | 'CUSTOM';

export type InventoryMode =
  | 'QUANTITY_BASED'
  | 'VARIANT_BASED'
  | 'AVAILABILITY_BASED'
  | 'WEIGHT_BASED';

export type StoreStatus = 'PENDING_APPROVAL' | 'ACTIVE' | 'SUSPENDED' | 'REJECTED';
export type DeliveryMode = 'OWN_DRIVER' | 'PLATFORM_DRIVER' | 'BOTH' | 'PICKUP_ONLY';
export type DriverType = 'INDEPENDENT' | 'STORE_DRIVER' | 'PROVIDER_DRIVER';
export type PricingType = 'FIXED' | 'STARTING_FROM' | 'HOURLY' | 'AFTER_INSPECTION' | 'CUSTOM_QUOTE';
export type PaymentMethod = 'CASH_ON_DELIVERY' | 'CARD_LATER' | 'WALLET_LATER';
export type ReceiptType = 'STORE_ORDER' | 'SERVICE_REQUEST' | 'CUSTOM_DELIVERY';
export type FulfillmentType = 'NOW' | 'SCHEDULED';
export type UnitType = 'PIECE' | 'KG' | 'GRAM' | 'LITER' | 'ML' | 'BOX' | 'PACK' | 'CUSTOM';
export type StockStatus =
  | 'IN_STOCK'
  | 'LOW_STOCK'
  | 'ONLY_FEW_LEFT'
  | 'USUALLY_AVAILABLE'
  | 'OUT_OF_STOCK'
  | 'UNAVAILABLE'
  | 'UNKNOWN';

export type InventoryMovementType =
  | 'STOCK_ADDED'
  | 'STOCK_REMOVED'
  | 'ORDER_SALE'
  | 'ORDER_CANCELLED'
  | 'MANUAL_ADJUSTMENT'
  | 'DAMAGED'
  | 'RETURNED'
  | 'EXPIRED';

export type ServiceRequestStatus =
  | 'PENDING'
  | 'ACCEPTED'
  | 'REJECTED'
  | 'WAITING_FOR_QUOTE'
  | 'QUOTE_SENT'
  | 'QUOTE_ACCEPTED'
  | 'IN_PROGRESS'
  | 'READY'
  | 'COMPLETED'
  | 'CANCELLED'
  | 'ISSUE_REPORTED';

export type User = {
  id: UUID;
  fullName: string;
  phone: string;
  email?: string;
  status: UserStatus;
  roles: RoleName[];
};

export type Role = {
  id: UUID;
  role: RoleName;
  entityType: EntityType;
  entityId?: UUID;
};

export type UserRole = Role;

export type ActiveRole = Role;

export type AuthUserSummary = {
  id: UUID;
  fullName: string;
  phone: string;
  email?: string;
  status: UserStatus;
  roles: RoleName[];
};

export type AuthResponse = {
  user: AuthUserSummary;
  accessToken: string;
  refreshToken: string;
};

export type CurrentUser = {
  id: UUID;
  fullName: string;
  phone: string;
  email?: string;
  status: UserStatus;
  roles: UserRole[];
};

export type UserProfile = {
  id: UUID;
  fullName: string;
  phone: string;
  email?: string;
  status: UserStatus;
  lastLoginAt?: string;
  createdAt: string;
  updatedAt: string;
};

export type UpdateCurrentUserRequest = {
  fullName?: string;
  phone?: string;
  email?: string;
};

export type ClientAddress = {
  id: UUID;
  label: string;
  fullAddress: string;
  latitude?: number | string;
  longitude?: number | string;
  phoneNumber?: string;
  instructions?: string;
  defaultAddress: boolean;
  createdAt: string;
  updatedAt: string;
};

export type CreateClientAddressRequest = {
  label: string;
  fullAddress: string;
  latitude?: number;
  longitude?: number;
  phoneNumber?: string;
  instructions?: string;
  defaultAddress: boolean;
};

export type UpdateClientAddressRequest = Partial<CreateClientAddressRequest>;

export type RegisterRequest = {
  fullName: string;
  phone: string;
  email?: string;
  password: string;
};

export type LoginRequest = {
  phone: string;
  password: string;
};

export type StoreType = {
  code: StoreTypeCode;
  displayName: string;
  active?: boolean;
};

export type StoreTypeConfig = {
  storeTypeCode: StoreTypeCode;
  displayName: string;
  inventoryMode: InventoryMode;
  enabledFeatures: string[];
  requiredProductFields: string[];
  optionalProductFields: string[];
  supportsModifiers: boolean;
  supportsVariants: boolean;
  supportsExpiryDate: boolean;
  supportsWeightItems: boolean;
  supportsPreparationTime: boolean;
  supportsPrescriptionFlag: boolean;
};

export type Store = {
  id: UUID;
  ownerUserId?: UUID;
  name: string;
  description?: string;
  storeTypeCode: StoreTypeCode;
  status: StoreStatus;
  deliveryMode: DeliveryMode;
  phone?: string;
  address?: string;
  openNow?: boolean;
  latitude?: string;
  longitude?: string;
  openingHours?: Record<string, unknown>;
  minimumOrderAmount?: Money;
  averagePreparationMinutes?: number;
  ratingAverage: Money;
  ratingCount: number;
  createdAt?: string;
  updatedAt?: string;
};

export type Product = {
  id: UUID;
  storeId: UUID;
  categoryId?: UUID;
  name: string;
  description?: string;
  price: Money;
  imageUrl?: string;
  stockStatus: StockStatus;
  active?: boolean;
  isAvailable: boolean;
  attributes?: ProductAttribute[];
  variants?: ProductVariant[];
};

export type ProductVariant = {
  id: UUID;
  productId: UUID;
  name: string;
  sku?: string;
  priceAdjustment: Money;
  stockQuantity: number;
  attributes: Record<string, string>;
  isAvailable: boolean;
};

export type ProductAttribute = {
  key: string;
  value: string;
};

export type Inventory = {
  id: UUID;
  storeId: UUID;
  productId: UUID;
  productVariantId?: UUID;
  inventoryMode: InventoryMode;
  quantity: number;
  reservedQuantity: number;
  lowStockThreshold: number;
  unitType?: UnitType;
  updatedAt?: string;
};

export type InventoryMovement = {
  id: UUID;
  storeId: UUID;
  productId: UUID;
  productVariantId?: UUID;
  movementType: InventoryMovementType;
  quantityChange: number;
  previousQuantity: number;
  newQuantity: number;
  reason?: string;
  referenceType?: string;
  referenceId?: UUID;
  createdAt: string;
};

export type OrderItem = {
  id: UUID;
  productId?: UUID;
  productVariantId?: UUID;
  productName: string;
  productImage?: string;
  quantity: number;
  unitPrice: Money;
  totalPrice: Money;
};

export type OrderAddress = {
  label?: string;
  fullAddress: string;
  latitude?: number;
  longitude?: number;
  phoneNumber?: string;
};

export type CreateOrderItemRequest = {
  productId: UUID;
  productVariantId?: UUID;
  quantity: number;
};

export type CreateOrderRequest = {
  storeId: UUID;
  paymentMethod: PaymentMethod;
  fulfillmentType: FulfillmentType;
  scheduledFor?: string;
  needsChange: boolean;
  cashAmountClientHas?: Money;
  notes?: string;
  addressId?: UUID;
  address?: OrderAddress;
  items: CreateOrderItemRequest[];
};

export type OrderTimelineEvent = {
  id: UUID;
  status: OrderStatus;
  title: string;
  description?: string;
  createdByUserId?: UUID;
  createdAt: string;
};

export type OrderSummary = {
  id: UUID;
  orderNumber: string;
  storeId: UUID;
  storeName: string;
  status: OrderStatus;
  paymentStatus: PaymentStatus;
  fulfillmentType: FulfillmentType;
  scheduledFor?: string;
  total: Money;
  createdAt: string;
};

export type OrderDetails = {
  id: UUID;
  orderNumber: string;
  clientUserId: UUID;
  storeId: UUID;
  storeName: string;
  status: OrderStatus;
  subtotal: Money;
  deliveryFee: Money;
  discount: Money;
  total: Money;
  paymentMethod: PaymentMethod;
  paymentStatus: PaymentStatus;
  fulfillmentType: FulfillmentType;
  scheduledFor?: string;
  needsChange: boolean;
  cashAmountClientHas?: Money;
  notes?: string;
  addressSnapshot?: string;
  items: OrderItem[];
  timeline: OrderTimelineEvent[];
  createdAt: string;
  updatedAt: string;
};

export type Order = OrderDetails;

export type CartItem = {
  productId: UUID;
  productVariantId?: UUID;
  storeId: UUID;
  storeName?: string;
  name: string;
  imageUrl?: string;
  unitPrice: Money;
  quantity: number;
};

export type CartState = {
  storeId?: UUID;
  items: CartItem[];
};

export type Driver = {
  id: UUID;
  userId: UUID;
  fullName: string;
  status: 'PENDING_APPROVAL' | 'ACTIVE' | 'SUSPENDED' | 'REJECTED';
  driverType: DriverType;
  vehicleType?: string;
};

export type Delivery = {
  id: UUID;
  orderId: UUID;
  orderNumber?: string;
  storeId?: UUID;
  storeName?: string;
  driverId?: UUID;
  driverName?: string;
  status: DeliveryStatus;
  deliveryFee: Money;
  pickupAddressSnapshot?: string;
  dropoffAddressSnapshot?: string;
  acceptedAt?: string;
  pickedUpAt?: string;
  deliveredAt?: string;
  failureReason?: string;
  cancellationReason?: string;
  createdAt?: string;
  updatedAt?: string;
};

export type DeliverySummary = {
  id: UUID;
  orderId: UUID;
  orderNumber?: string;
  storeName?: string;
  driverId?: UUID;
  status: DeliveryStatus;
  deliveryFee: Money;
  pickupAddressSnapshot?: string;
  dropoffAddressSnapshot?: string;
  createdAt: string;
  updatedAt: string;
};

export type DriverEarningsSummary = {
  completedDeliveries: number;
  totalDeliveryFees: Money;
  collectedCashTotal: Money;
  settledCashTotal: Money;
  unsettledCashTotal: Money;
  note: string;
};

export type CreateDriverProfileRequest = {
  driverType: DriverType;
  vehicleType?: string;
  phoneNumber?: string;
};

export type DriverProfile = Driver;
export type AdminDriverProfile = Driver & {
  phoneNumber?: string;
  createdAt?: string;
  updatedAt?: string;
};

export type RequestDeliveryRequest = {
  pickupAddress?: string;
  dropoffAddress?: string;
  deliveryFee?: Money;
};

export type Payment = {
  id: UUID;
  orderId?: UUID;
  deliveryId?: UUID;
  status: PaymentStatus;
  method: PaymentMethod;
  amount: Money;
  currency: string;
  collectedByDriverId?: UUID;
  collectedAmount?: Money;
  collectedAt?: string;
  cashMismatch?: boolean;
  collectionNote?: string;
};

export type CashSettlement = {
  id: UUID;
  driverId: UUID;
  driverName?: string;
  amount: Money;
  note?: string;
  createdByUserId?: UUID;
  createdByUserName?: string;
  createdAt: string;
};

export type CreateCashSettlementRequest = {
  driverId: UUID;
  amount: Money;
  note?: string;
};

export type Receipt = {
  id: UUID;
  receiptNumber: string;
  receiptType: ReceiptType;
  orderId?: UUID;
  orderNumber?: string;
  storeId?: UUID;
  storeName?: string;
  clientUserId?: UUID;
  clientName?: string;
  paymentId?: UUID;
  subtotal?: Money;
  deliveryFee?: Money;
  discount?: Money;
  totalAmount: Money;
  paymentMethod?: PaymentMethod;
  paymentStatus?: PaymentStatus;
  snapshotJson?: string;
  createdAt: string;
};

export type CashCollectionRequest = {
  collectedAmount: Money;
  needsChange?: boolean;
  note?: string;
};

export type CashCollectionResponse = {
  payment: Payment;
  receipt: Receipt;
};

export type AdminDecisionRequest = {
  reason?: string;
};

export type ServiceProvider = {
  id: UUID;
  ownerUserId?: UUID;
  name: string;
  phoneNumber?: string;
  address?: string;
  city?: string;
  active: boolean;
  createdAt?: string;
  updatedAt?: string;
};

export type Service = {
  id: UUID;
  serviceProviderId: UUID;
  serviceProviderName?: string;
  name: string;
  description?: string;
  basePrice?: Money;
  pricingType: PricingType;
  active: boolean;
  createdAt?: string;
  updatedAt?: string;
};

export type ServiceRequest = {
  id: UUID;
  clientId?: UUID;
  clientUserId?: UUID;
  clientName?: string;
  serviceProviderId?: UUID;
  serviceProviderName?: string;
  serviceId?: UUID;
  serviceName?: string;
  status: ServiceRequestStatus;
  description?: string;
  requestedLocationSnapshot?: string;
  quotedAmount?: Money;
  createdAt: string;
  updatedAt?: string;
};

export type CreateServiceProviderRequest = {
  name: string;
  phoneNumber?: string;
  address?: string;
  city?: string;
};

export type UpdateServiceProviderRequest = Partial<CreateServiceProviderRequest> & {
  active?: boolean;
};

export type CreateServiceOfferingRequest = {
  name: string;
  description?: string;
  basePrice?: Money;
  pricingType?: PricingType;
  active?: boolean;
};

export type UpdateServiceOfferingRequest = Partial<CreateServiceOfferingRequest>;

export type CreateServiceRequest = {
  serviceProviderId?: UUID;
  serviceId?: UUID;
  description: string;
  requestedLocation?: Record<string, unknown>;
};

export type QuoteServiceRequestRequest = {
  quotedAmount: Money;
};

export type Notification = {
  id: UUID;
  userId: UUID;
  title: string;
  body: string;
  readAt?: string;
  createdAt: string;
};

export type UnreadNotificationCount = {
  unreadCount: number;
};

export type SupportTicket = {
  id: UUID;
  userId: UUID;
  userName?: string;
  subject: string;
  description: string;
  status: SupportTicketStatus;
  relatedOrderId?: UUID;
  relatedDeliveryId?: UUID;
  relatedServiceRequestId?: UUID;
  adminNote?: string;
  createdAt: string;
  updatedAt?: string;
};

export type SupportTicketStatus = 'OPEN' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED';

export type CreateSupportTicketRequest = {
  subject: string;
  description: string;
  relatedOrderId?: UUID;
  relatedDeliveryId?: UUID;
  relatedServiceRequestId?: UUID;
};

export type UpdateSupportTicketStatusRequest = {
  status: SupportTicketStatus;
  adminNote?: string;
};
