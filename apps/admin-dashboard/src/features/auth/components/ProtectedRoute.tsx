import { useEffect } from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { hasAdminRole, useAuthStore } from '../store/authStore';

export function ProtectedRoute() {
  const { bootstrap, bootstrapping, user } = useAuthStore();

  useEffect(() => {
    void bootstrap();
  }, [bootstrap]);

  if (bootstrapping) {
    return <main className="centered-page">Loading admin dashboard...</main>;
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (!hasAdminRole(user)) {
    return <main className="centered-page">This dashboard requires the ADMIN role.</main>;
  }

  return <Outlet />;
}
