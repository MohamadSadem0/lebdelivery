import { useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import { NavLink, Outlet } from 'react-router-dom';
import { getMyStores } from '../../features/stores/api/storesApi';
import { useStoreSelectionStore } from '../../features/stores/store/storeSelectionStore';
import { businessRoles, useAuthStore } from '../../features/auth/store/authStore';
import { getMyProviders } from '../../features/providers/api/providersApi';
import { useProviderSelectionStore } from '../../features/providers/store/providerSelectionStore';

const links = [
  { to: '/stores', label: 'My Stores' },
  { to: '/orders', label: 'Orders' },
  { to: '/products', label: 'Products' },
  { to: '/inventory', label: 'Inventory' },
  { to: '/deliveries', label: 'Deliveries' },
  { to: '/receipts', label: 'Receipts' },
  { to: '/providers', label: 'Providers' },
  { to: '/services', label: 'Services' },
  { to: '/service-requests', label: 'Service Requests' },
  { to: '/notifications', label: 'Notifications' }
];

export function DashboardLayout() {
  const { user, activeRole, selectActiveRole, logout } = useAuthStore();
  const roles = businessRoles(user);
  const { selectedStoreId, setSelectedStoreId } = useStoreSelectionStore();
  const { selectedProviderId, setSelectedProviderId } = useProviderSelectionStore();
  const storesQuery = useQuery({ queryKey: ['my-stores'], queryFn: getMyStores });
  const providersQuery = useQuery({ queryKey: ['my-providers'], queryFn: getMyProviders });

  const stores = storesQuery.data ?? [];
  const providers = providersQuery.data ?? [];

  useEffect(() => {
    if (!selectedStoreId && stores.length > 0) {
      setSelectedStoreId(stores[0].id);
    }
  }, [selectedStoreId, setSelectedStoreId, stores]);

  useEffect(() => {
    if (!selectedProviderId && providers.length > 0) {
      setSelectedProviderId(providers[0].id);
    }
  }, [selectedProviderId, setSelectedProviderId, providers]);

  return (
    <div className="dashboard-shell">
      <aside className="sidebar">
        <div>
          <p className="eyebrow">Lebanon Platform</p>
          <h2>Business</h2>
        </div>
        <nav>
          {links.map((link) => (
            <NavLink key={link.to} to={link.to} className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}>
              {link.label}
            </NavLink>
          ))}
        </nav>
        <button className="ghost-button" onClick={() => logout()} type="button">
          Sign out
        </button>
      </aside>
      <main className="dashboard-main">
        <header className="topbar">
          <div>
            <p className="muted">Signed in as</p>
            <strong>{user?.fullName}</strong>
          </div>
          <label>
            Active role
            <select
              value={activeRole?.id ?? ''}
              onChange={(event) => {
                const next = roles.find((role) => role.id === event.target.value);
                if (next) {
                  void selectActiveRole(next);
                }
              }}
            >
              <option value="">Choose role</option>
              {roles.map((role) => (
                <option key={role.id} value={role.id}>
                  {role.role}
                </option>
              ))}
            </select>
          </label>
          <label>
            Store
            <select value={selectedStoreId ?? ''} onChange={(event) => setSelectedStoreId(event.target.value || undefined)}>
              <option value="">No store selected</option>
              {stores.map((store) => (
                <option key={store.id} value={store.id}>
                  {store.name}
                </option>
              ))}
            </select>
          </label>
          <label>
            Provider
            <select value={selectedProviderId ?? ''} onChange={(event) => setSelectedProviderId(event.target.value || undefined)}>
              <option value="">No provider selected</option>
              {providers.map((provider) => (
                <option key={provider.id} value={provider.id}>
                  {provider.name}
                </option>
              ))}
            </select>
          </label>
        </header>
        <Outlet />
      </main>
    </div>
  );
}
