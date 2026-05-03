import { QueryClientProvider } from '@tanstack/react-query';
import { useEffect } from 'react';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { DeliveryLayout } from '../components/layout/DeliveryLayout';
import { ProtectedRoute } from '../features/auth/components/ProtectedRoute';
import { LoginPage } from '../features/auth/pages/LoginPage';
import { useAuthStore } from '../features/auth/store/authStore';
import { ActiveDeliveryPage } from '../features/delivery/pages/ActiveDeliveryPage';
import { EarningsPage } from '../features/delivery/pages/EarningsPage';
import { HistoryPage } from '../features/delivery/pages/HistoryPage';
import { JobDetailsPage } from '../features/delivery/pages/JobDetailsPage';
import { JobsPage } from '../features/delivery/pages/JobsPage';
import { NotificationsPage } from '../features/delivery/pages/NotificationsPage';
import { OverviewPage } from '../features/delivery/pages/OverviewPage';
import { ProfilePage } from '../features/delivery/pages/ProfilePage';
import { SupportPage } from '../features/delivery/pages/SupportPage';
import { queryClient } from '../lib/queryClient';

function Bootstrapper() {
  const bootstrap = useAuthStore((state) => state.bootstrap);
  useEffect(() => {
    void bootstrap();
  }, [bootstrap]);
  return null;
}

export function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <Bootstrapper />
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route element={<ProtectedRoute />}>
            <Route element={<DeliveryLayout />}>
              <Route index element={<OverviewPage />} />
              <Route path="/profile" element={<ProfilePage />} />
              <Route path="/jobs" element={<JobsPage />} />
              <Route path="/jobs/:deliveryId" element={<JobDetailsPage />} />
              <Route path="/active" element={<ActiveDeliveryPage />} />
              <Route path="/earnings" element={<EarningsPage />} />
              <Route path="/history" element={<HistoryPage />} />
              <Route path="/notifications" element={<NotificationsPage />} />
              <Route path="/support" element={<SupportPage />} />
            </Route>
          </Route>
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </QueryClientProvider>
  );
}
