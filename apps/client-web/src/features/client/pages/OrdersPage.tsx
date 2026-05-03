import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Link, useNavigate } from 'react-router-dom';
import { formatDateTime, formatMoney, formatStatus } from '../../../lib/formatters';
import { getOrders, reorder } from '../api/clientApi';

export function OrdersPage() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const orders = useQuery({ queryKey: ['client-orders'], queryFn: getOrders });
  const reorderMutation = useMutation({
    mutationFn: reorder,
    onSuccess(order) {
      void queryClient.invalidateQueries({ queryKey: ['client-orders'] });
      navigate(`/orders/${order.id}`);
    }
  });

  return (
    <section className="stack">
      <div className="page-header">
        <h1>Orders</h1>
        <p className="muted">Track status changes and reorder with current availability checks.</p>
      </div>
      {orders.isLoading ? <p className="muted">Loading orders...</p> : null}
      {reorderMutation.isError ? <p className="error-text">Could not reorder. Product availability or stock may have changed.</p> : null}
      <div className="stack">
        {orders.data?.map((order) => (
          <article className="card row-card" key={order.id}>
            <div>
              <h2><Link to={`/orders/${order.id}`}>{order.orderNumber}</Link></h2>
              <p>{order.storeName} - {formatStatus(order.status)} - {formatMoney(order.total)}</p>
              <p>{order.fulfillmentType === 'SCHEDULED' ? `Scheduled: ${formatDateTime(order.scheduledFor)}` : 'Order now'}</p>
            </div>
            <button className="secondary-button" onClick={() => reorderMutation.mutate(order.id)}>Reorder</button>
          </article>
        ))}
      </div>
    </section>
  );
}
