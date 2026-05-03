import { Link, NavLink, Outlet } from 'react-router-dom';
import { useCartStore } from '../../features/cart/store/cartStore';
import { useAuthStore } from '../../features/auth/store/authStore';

export function AppLayout() {
  const { user, logout } = useAuthStore();
  const cartCount = useCartStore((state) => state.items.reduce((sum, item) => sum + item.quantity, 0));

  return (
    <div className="app-shell">
      <header className="topbar">
        <Link className="brand" to="/">
          Lebanon Commerce
        </Link>
        <nav className="nav">
          <NavLink to="/stores">Stores</NavLink>
          <NavLink to="/cart">Cart ({cartCount})</NavLink>
          {user ? (
            <>
              <NavLink to="/orders">Orders</NavLink>
              <NavLink to="/addresses">Addresses</NavLink>
              <NavLink to="/receipts">Receipts</NavLink>
              <NavLink to="/notifications">Notifications</NavLink>
              <NavLink to="/support">Support</NavLink>
              <button className="link-button" onClick={() => void logout()}>Logout</button>
            </>
          ) : (
            <NavLink to="/login">Sign in</NavLink>
          )}
        </nav>
      </header>
      <main className="page">
        <Outlet />
      </main>
    </div>
  );
}
