import { useEffect } from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { businessRoles, useAuthStore } from '../store/authStore';

export function ProtectedRoute() {
  const { bootstrap, bootstrapping, user } = useAuthStore();

  useEffect(() => {
    bootstrap();
  }, [bootstrap]);

  if (bootstrapping) {
    return <main className="centered-page">Loading dashboard...</main>;
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (businessRoles(user).length === 0) {
    return <main className="centered-page">This dashboard requires a business or admin role.</main>;
  }

  return <Outlet />;
}
