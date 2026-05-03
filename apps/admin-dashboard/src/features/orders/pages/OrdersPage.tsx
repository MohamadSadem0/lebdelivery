import { useQuery } from '@tanstack/react-query';
import { StateBlock } from '../../../components/feedback/StateBlock';
import { formatDate, formatMoney, label } from '../../../lib/formatters';
import { getAdminOrders } from '../../admin/api/adminApi';

export function OrdersPage() {
  const ordersQuery = useQuery({ queryKey: ['admin-orders'], queryFn: getAdminOrders });
  const orders = ordersQuery.data ?? [];

  return (
    <section className="page-section">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Marketplace activity</p>
          <h1>Orders</h1>
        </div>
      </div>

      {ordersQuery.isLoading ? <StateBlock title="Loading orders" /> : null}
      {ordersQuery.isError ? <StateBlock title="Could not load orders" /> : null}
      {!ordersQuery.isLoading && orders.length === 0 ? <StateBlock title="No orders found" /> : null}

      {orders.length > 0 ? (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Order</th>
                <th>Store</th>
                <th>Status</th>
                <th>Fulfillment</th>
                <th>Payment</th>
                <th>Total</th>
                <th>Created</th>
              </tr>
            </thead>
            <tbody>
              {orders.map((order) => (
                <tr key={order.id}>
                  <td>
                    <strong>{order.orderNumber}</strong>
                    <span className="muted block">{order.id}</span>
                  </td>
                  <td>
                    {order.storeName}
                    <span className="muted block">{order.storeId}</span>
                  </td>
                  <td><span className={`status-pill ${order.status.toLowerCase()}`}>{label(order.status)}</span></td>
                  <td>
                    {label(order.fulfillmentType)}
                    {order.scheduledFor ? <span className="muted block">{formatDate(order.scheduledFor)}</span> : null}
                  </td>
                  <td>{label(order.paymentStatus)}</td>
                  <td>{formatMoney(order.total)}</td>
                  <td>{formatDate(order.createdAt)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : null}
    </section>
  );
}
