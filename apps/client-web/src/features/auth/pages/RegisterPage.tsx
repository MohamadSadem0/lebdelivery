import { FormEvent, useState } from 'react';
import { Link, Navigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';

export function RegisterPage() {
  const { user, loading, error, register } = useAuthStore();
  const [fullName, setFullName] = useState('');
  const [phone, setPhone] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  if (user) {
    return <Navigate to="/stores" replace />;
  }

  async function submit(event: FormEvent) {
    event.preventDefault();
    await register({ fullName, phone, email: email || undefined, password });
  }

  return (
    <main className="auth-page">
      <form className="auth-card" onSubmit={submit}>
        <div>
          <p className="eyebrow">Client Website</p>
          <h1>Create your account</h1>
        </div>
        <label>
          Full name
          <input value={fullName} onChange={(event) => setFullName(event.target.value)} />
        </label>
        <label>
          Phone
          <input value={phone} onChange={(event) => setPhone(event.target.value)} placeholder="+96100000000" />
        </label>
        <label>
          Email
          <input value={email} onChange={(event) => setEmail(event.target.value)} />
        </label>
        <label>
          Password
          <input type="password" value={password} onChange={(event) => setPassword(event.target.value)} />
        </label>
        <button className="primary-button" disabled={loading} type="submit">
          {loading ? 'Creating...' : 'Create account'}
        </button>
        {error ? <p className="error-text">{error}</p> : null}
        <Link to="/login">Already have an account?</Link>
      </form>
    </main>
  );
}
