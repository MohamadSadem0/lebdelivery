import { FormEvent, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { StateBlock } from '../../../components/feedback/StateBlock';
import { formatMoney } from '../../../lib/formatters';
import { useProviderSelectionStore } from '../../providers/store/providerSelectionStore';
import { createService, getProviderServices } from '../api/servicesApi';

export function ServicesPage() {
  const selectedProviderId = useProviderSelectionStore((state) => state.selectedProviderId);
  const queryClient = useQueryClient();
  const [name, setName] = useState('');
  const [basePrice, setBasePrice] = useState('');
  const servicesQuery = useQuery({
    queryKey: ['provider-services', selectedProviderId],
    queryFn: () => getProviderServices(selectedProviderId as string),
    enabled: Boolean(selectedProviderId)
  });
  const createMutation = useMutation({
    mutationFn: () => createService(selectedProviderId as string, { name, basePrice: basePrice || undefined, pricingType: 'FIXED' }),
    onSuccess: async () => {
      setName('');
      setBasePrice('');
      await queryClient.invalidateQueries({ queryKey: ['provider-services', selectedProviderId] });
    }
  });

  function submit(event: FormEvent) {
    event.preventDefault();
    if (selectedProviderId && name.trim()) {
      createMutation.mutate();
    }
  }

  if (!selectedProviderId) {
    return <StateBlock title="Choose a provider" message="Select or create a provider before managing services." />;
  }

  const services = servicesQuery.data ?? [];

  return (
    <section className="page-section">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Service catalog</p>
          <h1>Services</h1>
        </div>
      </div>

      <form className="inline-form" onSubmit={submit}>
        <input value={name} onChange={(event) => setName(event.target.value)} placeholder="Service name" />
        <input value={basePrice} onChange={(event) => setBasePrice(event.target.value)} placeholder="Base price" />
        <button className="primary-button" disabled={createMutation.isPending} type="submit">Create service</button>
      </form>

      {servicesQuery.isLoading ? <StateBlock title="Loading services" /> : null}
      {servicesQuery.isError ? <StateBlock title="Could not load services" /> : null}
      {!servicesQuery.isLoading && services.length === 0 ? <StateBlock title="No services yet" /> : null}

      {services.length > 0 ? (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Service</th>
                <th>Pricing</th>
                <th>Base price</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {services.map((service) => (
                <tr key={service.id}>
                  <td>
                    <strong>{service.name}</strong>
                    <span className="muted block">{service.description || service.id}</span>
                  </td>
                  <td>{service.pricingType.replaceAll('_', ' ')}</td>
                  <td>{formatMoney(service.basePrice)}</td>
                  <td>{service.active ? 'Active' : 'Inactive'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : null}
    </section>
  );
}
