# Business Dashboard

React/Vite dashboard shell for store owners and, later, service providers.

## Run

```bash
cp .env.example .env
npm install
npm run business
```

PowerShell:

```powershell
Copy-Item apps/business-dashboard/.env.example apps/business-dashboard/.env
npm install
npm run business
```

The default API URL is:

```text
VITE_API_BASE_URL=http://localhost:8080/api/v1
```

Current MVP pages focus on store-owner operations: stores, orders, products, inventory, deliveries, and receipts.
