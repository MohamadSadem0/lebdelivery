import { NavLink, Outlet } from 'react-router-dom';
import { useAuthStore } from '../../features/auth/store/authStore';

export function DeliveryLayout() {
  const { user, activeRole, logout } = useAuthStore();

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div>
          <p className="eyebrow">Delivery</p>
          <h1>Driver portal</h1>
          <p className="muted">{user?.fullName ?? 'Not signed in'}</p>
          <p className="muted">{activeRole?.role ?? 'No driver role selected'}</p>
        </div>
        <nav className="nav">
          <NavLink to="/">Overview</NavLink>
          <NavLink to="/profile">Profile</NavLink>
          <NavLink to="/jobs">Available jobs</NavLink>
          <NavLink to="/active">Active delivery</NavLink>
          <NavLink to="/earnings">Earnings</NavLink>
          <NavLink to="/history">History</NavLink>
          <NavLink to="/notifications">Notifications</NavLink>
          <NavLink to="/support">Support</NavLink>
        </nav>
        <button className="secondary-button" onClick={() => void logout()}>Logout</button>
      </aside>
      <main className="page">
        <Outlet />
      </main>
    </div>
  );
}
