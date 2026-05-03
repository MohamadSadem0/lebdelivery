import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { FormEvent, useState } from 'react';
import { createAddress, getAddresses, setDefaultAddress } from '../api/clientApi';

export function AddressesPage() {
  const queryClient = useQueryClient();
  const addresses = useQuery({ queryKey: ['client-addresses'], queryFn: getAddresses });
  const [label, setLabel] = useState('Home');
  const [fullAddress, setFullAddress] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [instructions, setInstructions] = useState('');

  const invalidate = () => queryClient.invalidateQueries({ queryKey: ['client-addresses'] });
  const createMutation = useMutation({
    mutationFn: createAddress,
    onSuccess() {
      setFullAddress('');
      setPhoneNumber('');
      setInstructions('');
      void invalidate();
    }
  });
  const defaultMutation = useMutation({ mutationFn: setDefaultAddress, onSuccess: invalidate });

  function submit(event: FormEvent) {
    event.preventDefault();
    if (!label.trim() || !fullAddress.trim()) {
      return;
    }
    createMutation.mutate({
      label,
      fullAddress,
      phoneNumber: phoneNumber || undefined,
      instructions: instructions || undefined,
      defaultAddress: addresses.data?.length === 0
    });
  }

  return (
    <section className="stack">
      <div className="page-header">
        <h1>Saved addresses</h1>
        <p className="muted">Use saved addresses for faster checkout.</p>
      </div>
      <form className="form-card" onSubmit={submit}>
        <label>
          Label
          <input value={label} onChange={(event) => setLabel(event.target.value)} />
        </label>
        <label>
          Full address
          <input value={fullAddress} onChange={(event) => setFullAddress(event.target.value)} placeholder="Hamra, Beirut" />
        </label>
        <label>
          Phone number
          <input value={phoneNumber} onChange={(event) => setPhoneNumber(event.target.value)} />
        </label>
        <label>
          Instructions
          <input value={instructions} onChange={(event) => setInstructions(event.target.value)} />
        </label>
        <button className="primary-button" disabled={createMutation.isPending} type="submit">
          {createMutation.isPending ? 'Saving...' : 'Save address'}
        </button>
      </form>
      <div className="card-grid">
        {addresses.data?.map((address) => (
          <article className="card" key={address.id}>
            <h2>{address.label}{address.defaultAddress ? ' - Default' : ''}</h2>
            <p>{address.fullAddress}</p>
            {address.instructions ? <p>{address.instructions}</p> : null}
            {!address.defaultAddress ? (
              <button className="secondary-button" onClick={() => defaultMutation.mutate(address.id)}>Set default</button>
            ) : null}
          </article>
        ))}
      </div>
    </section>
  );
}
