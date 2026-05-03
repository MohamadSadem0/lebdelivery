import { useQuery } from '@tanstack/react-query';
import { useParams } from 'react-router-dom';
import { formatDateTime, formatMoney, formatStatus } from '../../../lib/formatters';
import { getOrder, getOrderTimeline } from '../api/clientApi';

export function OrderTrackingPage() {
  const { orderId = '' } = useParams();
  const order = useQuery({ queryKey: ['client-order', orderId], queryFn: () => getOrder(orderId), enabled: Boolean(orderId) });
  const timeline = useQuery({ queryKey: ['client-order-timeline', orderId], queryFn: () => getOrderTimeline(orderId), enabled: Boolean(orderId) });

  if (order.isLoading) {
    return <p className="muted">Loading order...</p>;
  }
  if (!order.data) {
    return <p className="error-text">Order is unavailable.</p>;
  }

  return (
    <section className="stack">
      <div className="page-header">
        <p className="eyebrow">{formatStatus(order.data.status)}</p>
        <h1>{order.data.orderNumber}</h1>
        <p className="muted">{order.data.storeName} - {formatMoney(order.data.total)}</p>
        <p className="muted">{order.data.fulfillmentType === 'SCHEDULED' ? `Scheduled: ${formatDateTime(order.data.scheduledFor)}` : 'Order now'}</p>
      </div>
      <section className="card">
        <h2>Items</h2>
        {order.data.items.map((item) => (
          <p key={item.id}>{item.productName} x {item.quantity} - {formatMoney(item.totalPrice)}</p>
        ))}
      </section>
      <section className="card">
        <h2>Timeline</h2>
        <div className="timeline">
          {(timeline.data?.events ?? order.data.timeline).map((event) => (
            <div className="timeline-item" key={event.id}>
              <strong>{event.title}</strong>
              <span>{formatStatus(event.status)}</span>
              {event.description ? <p>{event.description}</p> : null}
            </div>
          ))}
        </div>
      </section>
    </section>
  );
}
