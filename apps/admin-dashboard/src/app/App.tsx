import { QueryClientProvider } from '@tanstack/react-query';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { AdminLayout } from '../components/layout/AdminLayout';
import { ProtectedRoute } from '../features/auth/components/ProtectedRoute';
import { LoginPage } from '../features/auth/pages/LoginPage';
import { DriversPage } from '../features/drivers/pages/DriversPage';
import { DeliveriesPage } from '../features/deliveries/pages/DeliveriesPage';
import { OverviewPage } from '../features/overview/pages/OverviewPage';
import { NotificationsPage } from '../features/notifications/pages/NotificationsPage';
import { OrdersPage } from '../features/orders/pages/OrdersPage';
import { PaymentsPage } from '../features/payments/pages/PaymentsPage';
import { PlaceholderPage } from '../features/placeholders/PlaceholderPage';
import { ReceiptsPage } from '../features/receipts/pages/ReceiptsPage';
import { StoresPage } from '../features/stores/pages/StoresPage';
import { SupportTicketsPage } from '../features/support/pages/SupportTicketsPage';
import { UsersPage } from '../features/users/pages/UsersPage';
import { queryClient } from '../lib/queryClient';

export function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route element={<ProtectedRoute />}>
            <Route element={<AdminLayout />}>
              <Route index element={<Navigate to="/overview" replace />} />
              <Route path="/overview" element={<OverviewPage />} />
              <Route path="/users" element={<UsersPage />} />
              <Route path="/stores" element={<StoresPage />} />
              <Route path="/drivers" element={<DriversPage />} />
              <Route path="/orders" element={<OrdersPage />} />
              <Route path="/deliveries" element={<DeliveriesPage />} />
              <Route path="/receipts" element={<ReceiptsPage />} />
              <Route path="/payments" element={<PaymentsPage />} />
              <Route path="/support" element={<SupportTicketsPage />} />
              <Route path="/notifications" element={<NotificationsPage />} />
              <Route path="/settings" element={<PlaceholderPage title="Settings" message="Runtime settings stay placeholder-only until production configuration is ready." />} />
            </Route>
          </Route>
          <Route path="*" element={<Navigate to="/overview" replace />} />
        </Routes>
      </BrowserRouter>
    </QueryClientProvider>
  );
}
