import { NavLink, Outlet } from 'react-router-dom';
import { useAuthStore } from '../../features/auth/store/authStore';

const links = [
  { to: '/overview', label: 'Overview' },
  { to: '/users', label: 'Users' },
  { to: '/stores', label: 'Stores' },
  { to: '/drivers', label: 'Drivers' },
  { to: '/orders', label: 'Orders' },
  { to: '/deliveries', label: 'Deliveries' },
  { to: '/receipts', label: 'Receipts' },
  { to: '/payments', label: 'Cash' },
  { to: '/support', label: 'Support' },
  { to: '/notifications', label: 'Notifications' },
  { to: '/settings', label: 'Settings' }
];

export function AdminLayout() {
  const { user, logout } = useAuthStore();

  return (
    <div className="dashboard-shell">
      <aside className="sidebar">
        <div>
          <p className="eyebrow">Lebanon Platform</p>
          <h2>Admin</h2>
        </div>
        <nav>
          {links.map((link) => (
            <NavLink key={link.to} to={link.to} className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}>
              {link.label}
            </NavLink>
          ))}
        </nav>
        <button className="ghost-button" onClick={() => void logout()} type="button">
          Sign out
        </button>
      </aside>
      <main className="dashboard-main">
        <header className="topbar">
          <div>
            <p className="muted">Administrator</p>
            <strong>{user?.fullName}</strong>
          </div>
          <span className="status-pill active">ADMIN</span>
        </header>
        <Outlet />
      </main>
    </div>
  );
}
