import { FormEvent, useState } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';

export function LoginPage() {
  const { user, loading, error, login } = useAuthStore();
  const location = useLocation();
  const [phone, setPhone] = useState('');
  const [password, setPassword] = useState('');

  if (user) {
    const from = typeof location.state === 'object' && location.state && 'from' in location.state ? String(location.state.from) : '/';
    return <Navigate to={from} replace />;
  }

  async function submit(event: FormEvent) {
    event.preventDefault();
    await login({ phone, password });
  }

  return (
    <main className="auth-page">
      <form className="auth-card" onSubmit={submit}>
        <div>
          <p className="eyebrow">Delivery Portal</p>
          <h1>Sign in to manage deliveries</h1>
          <p className="muted">Use an account with an independent driver or store driver role.</p>
        </div>
        <label>
          Phone
          <input value={phone} onChange={(event) => setPhone(event.target.value)} placeholder="+96100000000" />
        </label>
        <label>
          Password
          <input type="password" value={password} onChange={(event) => setPassword(event.target.value)} />
        </label>
        <button className="primary-button" disabled={loading} type="submit">
          {loading ? 'Signing in...' : 'Sign in'}
        </button>
        {error ? <p className="error-text">{error}</p> : null}
      </form>
    </main>
  );
}
