import { QueryClientProvider } from '@tanstack/react-query';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { DashboardLayout } from '../components/layout/DashboardLayout';
import { ProtectedRoute } from '../features/auth/components/ProtectedRoute';
import { LoginPage } from '../features/auth/pages/LoginPage';
import { DeliveriesPage } from '../features/deliveries/pages/DeliveriesPage';
import { InventoryPage } from '../features/inventory/pages/InventoryPage';
import { OrdersPage } from '../features/orders/pages/OrdersPage';
import { NotificationsPage } from '../features/notifications/pages/NotificationsPage';
import { ProductsPage } from '../features/products/pages/ProductsPage';
import { ProviderRequestsPage } from '../features/provider-requests/pages/ProviderRequestsPage';
import { ProvidersPage } from '../features/providers/pages/ProvidersPage';
import { ReceiptsPage } from '../features/receipts/pages/ReceiptsPage';
import { ServicesPage } from '../features/services/pages/ServicesPage';
import { StoresPage } from '../features/stores/pages/StoresPage';
import { queryClient } from '../lib/queryClient';

export function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route element={<ProtectedRoute />}>
            <Route element={<DashboardLayout />}>
              <Route index element={<Navigate to="/stores" replace />} />
              <Route path="/stores" element={<StoresPage />} />
              <Route path="/orders" element={<OrdersPage />} />
              <Route path="/products" element={<ProductsPage />} />
              <Route path="/inventory" element={<InventoryPage />} />
              <Route path="/deliveries" element={<DeliveriesPage />} />
              <Route path="/receipts" element={<ReceiptsPage />} />
              <Route path="/providers" element={<ProvidersPage />} />
              <Route path="/services" element={<ServicesPage />} />
              <Route path="/service-requests" element={<ProviderRequestsPage />} />
              <Route path="/notifications" element={<NotificationsPage />} />
            </Route>
          </Route>
          <Route path="*" element={<Navigate to="/stores" replace />} />
        </Routes>
      </BrowserRouter>
    </QueryClientProvider>
  );
}
