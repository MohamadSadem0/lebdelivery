# Store Owner Order Management Frontend TODO

No store owner dashboard or store mobile app exists yet.

When that frontend is scaffolded, add:

- Store orders list using `GET /api/v1/store-owner/stores/{storeId}/orders`.
- Order details using `GET /api/v1/store-owner/stores/{storeId}/orders/{orderId}`.
- Accept order action using `POST /api/v1/store-owner/stores/{storeId}/orders/{orderId}/accept`.
- Reject order action using `POST /api/v1/store-owner/stores/{storeId}/orders/{orderId}/reject`.
- Mark preparing action using `POST /api/v1/store-owner/stores/{storeId}/orders/{orderId}/mark-preparing`.
- Mark ready action using `POST /api/v1/store-owner/stores/{storeId}/orders/{orderId}/mark-ready`.

Frontend role checks are only for UX. The backend must continue enforcing store ownership and valid order status transitions.
