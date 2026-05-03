import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { FormEvent, useMemo, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { formatMoney, formatStatus } from '../../../lib/formatters';
import { failDelivery, getActiveDeliveries, markCashCollected, updateDeliveryStatus } from '../api/deliveryApi';

type DeliveryAction = 'arrived-pickup' | 'picked-up' | 'on-the-way' | 'arrived-destination' | 'delivered';

export function ActiveDeliveryPage() {
  const [searchParams] = useSearchParams();
  const queryClient = useQueryClient();
  const active = useQuery({ queryKey: ['active-deliveries'], queryFn: getActiveDeliveries });
  const [collectedAmount, setCollectedAmount] = useState('');
  const [failureReason, setFailureReason] = useState('');
  const selected = useMemo(() => {
    const deliveryId = searchParams.get('deliveryId');
    return active.data?.find((delivery) => delivery.id === deliveryId) ?? active.data?.[0];
  }, [active.data, searchParams]);

  const statusMutation = useMutation({
    mutationFn: (action: DeliveryAction) => selected ? updateDeliveryStatus(selected.id, action) : Promise.reject(new Error('No delivery selected')),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['active-deliveries'] })
  });
  const cashMutation = useMutation({
    mutationFn: () => selected ? markCashCollected(selected.id, { collectedAmount: collectedAmount || '0', note: 'Collected from delivery web MVP' }) : Promise.reject(new Error('No delivery selected')),
    onSuccess: () => {
      setCollectedAmount('');
      void queryClient.invalidateQueries({ queryKey: ['active-deliveries'] });
    }
  });
  const failMutation = useMutation({
    mutationFn: () => selected ? failDelivery(selected.id, failureReason || 'Delivery failed from delivery web MVP') : Promise.reject(new Error('No delivery selected')),
    onSuccess: () => {
      setFailureReason('');
      void queryClient.invalidateQueries({ queryKey: ['active-deliveries'] });
    }
  });

  function collectCash(event: FormEvent) {
    event.preventDefault();
    cashMutation.mutate();
  }

  return (
    <section className="stack">
      <div className="page-header">
        <h1>Active delivery</h1>
        <p className="muted">Update delivery milestones in order. Invalid transitions are rejected by the backend.</p>
      </div>
      {!selected && !active.isLoading ? <p className="muted">No active delivery.</p> : null}
      {selected ? (
        <article className="card">
          <h2>{selected.orderNumber ?? selected.id}</h2>
          <p>{selected.storeName ?? 'Store delivery'} - {formatStatus(selected.status)} - {formatMoney(selected.deliveryFee)}</p>
          <p>Pickup: {selected.pickupAddressSnapshot ?? 'Pending'}</p>
          <p>Dropoff: {selected.dropoffAddressSnapshot ?? 'Pending'}</p>
          <div className="button-row">
            <button className="secondary-button" onClick={() => statusMutation.mutate('arrived-pickup')}>Arrived pickup</button>
            <button className="secondary-button" onClick={() => statusMutation.mutate('picked-up')}>Picked up</button>
            <button className="secondary-button" onClick={() => statusMutation.mutate('on-the-way')}>On the way</button>
            <button className="secondary-button" onClick={() => statusMutation.mutate('arrived-destination')}>Arrived destination</button>
            <button className="primary-button" onClick={() => statusMutation.mutate('delivered')}>Delivered</button>
          </div>
          {statusMutation.isError ? <p className="error-text">Status update failed. Check the current delivery state.</p> : null}
          {selected.status === 'DELIVERED' ? (
            <form className="inline-form" onSubmit={collectCash}>
              <input value={collectedAmount} onChange={(event) => setCollectedAmount(event.target.value)} placeholder="Collected amount" inputMode="decimal" />
              <button className="primary-button" type="submit">Mark cash collected</button>
            </form>
          ) : null}
          <form className="inline-form" onSubmit={(event) => { event.preventDefault(); failMutation.mutate(); }}>
            <input value={failureReason} onChange={(event) => setFailureReason(event.target.value)} placeholder="Failure reason" />
            <button className="danger-button" type="submit">Mark failed</button>
          </form>
        </article>
      ) : null}
    </section>
  );
}
