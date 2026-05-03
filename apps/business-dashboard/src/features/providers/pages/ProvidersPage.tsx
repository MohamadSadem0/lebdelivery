import { FormEvent, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { StateBlock } from '../../../components/feedback/StateBlock';
import { formatDate } from '../../../lib/formatters';
import { createProvider, getMyProviders } from '../api/providersApi';
import { useProviderSelectionStore } from '../store/providerSelectionStore';

export function ProvidersPage() {
  const queryClient = useQueryClient();
  const providersQuery = useQuery({ queryKey: ['my-providers'], queryFn: getMyProviders });
  const { selectedProviderId, setSelectedProviderId } = useProviderSelectionStore();
  const [name, setName] = useState('');
  const [city, setCity] = useState('');
  const createMutation = useMutation({
    mutationFn: createProvider,
    onSuccess: async (provider) => {
      setName('');
      setCity('');
      setSelectedProviderId(provider.id);
      await queryClient.invalidateQueries({ queryKey: ['my-providers'] });
    }
  });

  function submit(event: FormEvent) {
    event.preventDefault();
    if (!name.trim()) {
      return;
    }
    createMutation.mutate({ name, city });
  }

  const providers = providersQuery.data ?? [];

  return (
    <section className="page-section">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Service provider system</p>
          <h1>Providers</h1>
        </div>
      </div>

      <form className="inline-form" onSubmit={submit}>
        <input value={name} onChange={(event) => setName(event.target.value)} placeholder="Provider name" />
        <input value={city} onChange={(event) => setCity(event.target.value)} placeholder="City" />
        <button className="primary-button" disabled={createMutation.isPending} type="submit">Create provider</button>
      </form>

      {providersQuery.isLoading ? <StateBlock title="Loading providers" /> : null}
      {providersQuery.isError ? <StateBlock title="Could not load providers" /> : null}
      {!providersQuery.isLoading && providers.length === 0 ? <StateBlock title="No providers yet" message="Create a provider profile to manage services and requests." /> : null}

      <div className="store-grid">
        {providers.map((provider) => (
          <article className={selectedProviderId === provider.id ? 'store-card selected' : 'store-card'} key={provider.id}>
            <div className="row-between">
              <h2>{provider.name}</h2>
              <span className={`status-pill ${provider.active ? 'active' : 'danger'}`}>{provider.active ? 'ACTIVE' : 'INACTIVE'}</span>
            </div>
            <dl className="metadata-grid">
              <div>
                <dt>City</dt>
                <dd>{provider.city || '-'}</dd>
              </div>
              <div>
                <dt>Phone</dt>
                <dd>{provider.phoneNumber || '-'}</dd>
              </div>
              <div>
                <dt>Created</dt>
                <dd>{formatDate(provider.createdAt)}</dd>
              </div>
            </dl>
            <button className="primary-button" type="button" onClick={() => setSelectedProviderId(provider.id)}>Use this provider</button>
          </article>
        ))}
      </div>
    </section>
  );
}
