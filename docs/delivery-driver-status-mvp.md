# Delivery Request and Driver Status MVP

This MVP adds the first delivery lifecycle without route optimization, live tracking, or payment collection.

Flow:

1. Store marks an order `READY_FOR_PICKUP`.
2. Store requests delivery for that order.
3. Delivery is created as `WAITING_FOR_DRIVER`.
4. Active independent drivers can list waiting jobs.
5. One driver accepts the job; the delivery row is locked during acceptance.
6. Driver can move the delivery through pickup, on-the-way, destination, and delivered states.

Current boundaries:

- Driver profiles are auto-activated for MVP so the driver app can exercise the flow.
- Admin approval should replace auto-activation before production.
- No delivery matching, batching, route optimization, live GPS, proof of delivery, or cash collection settlement is implemented yet.
- Redis should be used later for live driver location instead of writing frequent GPS updates to PostgreSQL.
