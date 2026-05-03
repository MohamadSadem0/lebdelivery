import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { FormEvent, useState } from 'react';
import { formatStatus } from '../../../lib/formatters';
import { createDriverProfile, getDriverProfile } from '../api/deliveryApi';

export function ProfilePage() {
  const queryClient = useQueryClient();
  const profile = useQuery({ queryKey: ['driver-profile'], queryFn: getDriverProfile });
  const [vehicleType, setVehicleType] = useState('Motorbike');
  const [phoneNumber, setPhoneNumber] = useState('');
  const mutation = useMutation({
    mutationFn: createDriverProfile,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['driver-profile'] })
  });

  function submit(event: FormEvent) {
    event.preventDefault();
    mutation.mutate({ driverType: 'INDEPENDENT', vehicleType, phoneNumber: phoneNumber || undefined });
  }

  return (
    <section className="stack">
      <div className="page-header">
        <h1>Driver profile</h1>
        <p className="muted">Production approval remains admin-controlled; dev profiles can be prepared here.</p>
      </div>
      {profile.data ? (
        <article className="card">
          <h2>{profile.data.fullName}</h2>
          <p>Status: {formatStatus(profile.data.status)}</p>
          <p>Type: {formatStatus(profile.data.driverType)}</p>
          <p>Vehicle: {profile.data.vehicleType ?? 'Not set'}</p>
        </article>
      ) : null}
      <form className="form-card" onSubmit={submit}>
        <label>
          Vehicle type
          <input value={vehicleType} onChange={(event) => setVehicleType(event.target.value)} />
        </label>
        <label>
          Driver phone
          <input value={phoneNumber} onChange={(event) => setPhoneNumber(event.target.value)} />
        </label>
        <button className="primary-button" disabled={mutation.isPending} type="submit">
          {mutation.isPending ? 'Saving...' : 'Create or update profile'}
        </button>
      </form>
    </section>
  );
}
