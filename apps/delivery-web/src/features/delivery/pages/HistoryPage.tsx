import { useQuery } from '@tanstack/react-query';
import { formatMoney, formatStatus } from '../../../lib/formatters';
import { getDeliveryHistory } from '../api/deliveryApi';

export function HistoryPage() {
  const history = useQuery({ queryKey: ['delivery-history'], queryFn: getDeliveryHistory });

  return (
    <section className="stack">
      <div className="page-header">
        <h1>Delivery history</h1>
        <p className="muted">Completed, failed, and cancelled deliveries for the authenticated driver.</p>
      </div>
      {history.isLoading ? <p className="muted">Loading history...</p> : null}
      {history.isError ? <p className="error-text">History is unavailable. Create an active driver profile first.</p> : null}
      <div className="card-grid">
        {history.data?.map((delivery) => (
          <article className="card" key={delivery.id}>
            <h2>{delivery.orderNumber ?? delivery.id}</h2>
            <p>{delivery.storeName ?? 'Store delivery'}</p>
            <p>{formatStatus(delivery.status)} - {formatMoney(delivery.deliveryFee)}</p>
            <p>Pickup: {delivery.pickupAddressSnapshot ?? 'Pending'}</p>
            <p>Dropoff: {delivery.dropoffAddressSnapshot ?? 'Pending'}</p>
          </article>
        ))}
      </div>
    </section>
  );
}
