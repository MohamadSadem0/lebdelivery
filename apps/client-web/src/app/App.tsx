import { QueryClientProvider } from '@tanstack/react-query';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { useEffect } from 'react';
import { AppLayout } from '../components/layout/AppLayout';
import { ProtectedRoute } from '../features/auth/components/ProtectedRoute';
import { LoginPage } from '../features/auth/pages/LoginPage';
import { RegisterPage } from '../features/auth/pages/RegisterPage';
import { useAuthStore } from '../features/auth/store/authStore';
import { CartPage } from '../features/cart/pages/CartPage';
import { HomePage } from '../features/catalog/pages/HomePage';
import { ProductDetailsPage } from '../features/catalog/pages/ProductDetailsPage';
import { StoreDetailsPage } from '../features/catalog/pages/StoreDetailsPage';
import { StoresPage } from '../features/catalog/pages/StoresPage';
import { AddressesPage } from '../features/client/pages/AddressesPage';
import { CheckoutPage } from '../features/client/pages/CheckoutPage';
import { NotificationsPage } from '../features/client/pages/NotificationsPage';
import { OrdersPage } from '../features/client/pages/OrdersPage';
import { OrderTrackingPage } from '../features/client/pages/OrderTrackingPage';
import { ReceiptsPage } from '../features/client/pages/ReceiptsPage';
import { SupportPage } from '../features/client/pages/SupportPage';
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
          <Route path="/register" element={<RegisterPage />} />
          <Route element={<AppLayout />}>
            <Route index element={<HomePage />} />
            <Route path="/stores" element={<StoresPage />} />
            <Route path="/stores/:storeId" element={<StoreDetailsPage />} />
            <Route path="/stores/:storeId/products/:productId" element={<ProductDetailsPage />} />
            <Route path="/cart" element={<CartPage />} />
            <Route element={<ProtectedRoute />}>
              <Route path="/checkout" element={<CheckoutPage />} />
              <Route path="/orders" element={<OrdersPage />} />
              <Route path="/orders/:orderId" element={<OrderTrackingPage />} />
              <Route path="/addresses" element={<AddressesPage />} />
              <Route path="/receipts" element={<ReceiptsPage />} />
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
