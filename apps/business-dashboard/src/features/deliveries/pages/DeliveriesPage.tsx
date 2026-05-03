import { useQuery } from '@tanstack/react-query';
import { StateBlock } from '../../../components/feedback/StateBlock';
import { formatDate, formatMoney } from '../../../lib/formatters';
import { useStoreSelectionStore } from '../../stores/store/storeSelectionStore';
import { getStoreDeliveries } from '../api/deliveriesApi';

export function DeliveriesPage() {
  const selectedStoreId = useStoreSelectionStore((state) => state.selectedStoreId);
  const deliveriesQuery = useQuery({
    queryKey: ['store-deliveries', selectedStoreId],
    queryFn: () => getStoreDeliveries(selectedStoreId as string),
    enabled: Boolean(selectedStoreId)
  });

  if (!selectedStoreId) {
    return <StateBlock title="Choose a store" message="Select a store from the top bar to view deliveries." />;
  }

  const deliveries = deliveriesQuery.data ?? [];

  return (
    <section className="page-section">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Delivery system</p>
          <h1>Delivery Requests</h1>
        </div>
      </div>

      {deliveriesQuery.isLoading ? <StateBlock title="Loading deliveries" /> : null}
      {deliveriesQuery.isError ? <StateBlock title="Could not load deliveries" /> : null}
      {!deliveriesQuery.isLoading && deliveries.length === 0 ? <StateBlock title="No delivery requests" message="Ready orders can request a platform driver from the Orders page." /> : null}

      {deliveries.length > 0 ? (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Order</th>
                <th>Status</th>
                <th>Driver</th>
                <th>Fee</th>
                <th>Created</th>
              </tr>
            </thead>
            <tbody>
              {deliveries.map((delivery) => (
                <tr key={delivery.id}>
                  <td>{delivery.orderNumber || delivery.orderId}</td>
                  <td><span className="status-pill pending">{delivery.status.replaceAll('_', ' ')}</span></td>
                  <td>{delivery.driverId || 'Unassigned'}</td>
                  <td>{formatMoney(delivery.deliveryFee)}</td>
                  <td>{formatDate(delivery.createdAt)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : null}
    </section>
  );
}
