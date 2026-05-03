# Client Web

React/Vite web client for the Lebanon Local Commerce & Delivery Platform.

## Run Locally

```bash
cp .env.example .env
npm install
npm run client-web
```

The backend must be running at `VITE_API_BASE_URL`, usually:

```text
http://localhost:8080/api/v1
```

For physical-device or LAN testing, use your computer IP instead of `localhost`.
