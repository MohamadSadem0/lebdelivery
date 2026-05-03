import { FormEvent, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { businessRoles, useAuthStore } from '../store/authStore';

export function LoginPage() {
  const { user, loading, error, login } = useAuthStore();
  const [phone, setPhone] = useState('');
  const [password, setPassword] = useState('');

  if (user && businessRoles(user).length > 0) {
    return <Navigate to="/stores" replace />;
  }

  async function submit(event: FormEvent) {
    event.preventDefault();
    await login({ phone, password });
  }

  return (
    <main className="login-page">
      <form className="login-card" onSubmit={submit}>
        <div>
          <p className="eyebrow">Business Dashboard</p>
          <h1>Operate your stores and services</h1>
          <p className="muted">Sign in with the same account used for store, provider, or admin roles.</p>
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
