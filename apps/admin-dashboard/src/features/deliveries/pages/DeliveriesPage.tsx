import { useQuery } from '@tanstack/react-query';
import { StateBlock } from '../../../components/feedback/StateBlock';
import { formatDate, formatMoney, label } from '../../../lib/formatters';
import { getAdminDeliveries } from '../../admin/api/adminApi';

export function DeliveriesPage() {
  const deliveriesQuery = useQuery({ queryKey: ['admin-deliveries'], queryFn: getAdminDeliveries });
  const deliveries = deliveriesQuery.data ?? [];

  return (
    <section className="page-section">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Delivery monitoring</p>
          <h1>Deliveries</h1>
        </div>
      </div>

      {deliveriesQuery.isLoading ? <StateBlock title="Loading deliveries" /> : null}
      {deliveriesQuery.isError ? <StateBlock title="Could not load deliveries" /> : null}
      {!deliveriesQuery.isLoading && deliveries.length === 0 ? <StateBlock title="No deliveries found" /> : null}

      {deliveries.length > 0 ? (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Delivery</th>
                <th>Order</th>
                <th>Store</th>
                <th>Driver</th>
                <th>Status</th>
                <th>Fee</th>
                <th>Created</th>
              </tr>
            </thead>
            <tbody>
              {deliveries.map((delivery) => (
                <tr key={delivery.id}>
                  <td>{delivery.id}</td>
                  <td>{delivery.orderNumber || delivery.orderId}</td>
                  <td>{delivery.storeName || '-'}</td>
                  <td>{delivery.driverId || 'Unassigned'}</td>
                  <td><span className={`status-pill ${delivery.status.toLowerCase()}`}>{label(delivery.status)}</span></td>
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
