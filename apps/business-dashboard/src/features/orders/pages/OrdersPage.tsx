import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import type { OrderStatus } from '@lebanon-platform/shared-types';
import { StateBlock } from '../../../components/feedback/StateBlock';
import { formatDate, formatMoney } from '../../../lib/formatters';
import { useStoreSelectionStore } from '../../stores/store/storeSelectionStore';
import { acceptOrder, markPreparing, markReady, rejectOrder, requestDelivery, getStoreOrders } from '../api/ordersApi';

export function OrdersPage() {
  const selectedStoreId = useStoreSelectionStore((state) => state.selectedStoreId);
  const queryClient = useQueryClient();
  const ordersQuery = useQuery({
    queryKey: ['store-orders', selectedStoreId],
    queryFn: () => getStoreOrders(selectedStoreId as string),
    enabled: Boolean(selectedStoreId)
  });

  const invalidateOrders = async () => {
    await queryClient.invalidateQueries({ queryKey: ['store-orders', selectedStoreId] });
  };

  const acceptMutation = useMutation({ mutationFn: (orderId: string) => acceptOrder(selectedStoreId as string, orderId), onSuccess: invalidateOrders });
  const rejectMutation = useMutation({
    mutationFn: (orderId: string) => {
      const reason = window.prompt('Reject reason', 'Item unavailable') || 'Rejected by store';
      return rejectOrder(selectedStoreId as string, orderId, reason);
    },
    onSuccess: invalidateOrders
  });
  const preparingMutation = useMutation({ mutationFn: (orderId: string) => markPreparing(selectedStoreId as string, orderId), onSuccess: invalidateOrders });
  const readyMutation = useMutation({ mutationFn: (orderId: string) => markReady(selectedStoreId as string, orderId), onSuccess: invalidateOrders });
  const deliveryMutation = useMutation({ mutationFn: (orderId: string) => requestDelivery(selectedStoreId as string, orderId), onSuccess: invalidateOrders });

  if (!selectedStoreId) {
    return <StateBlock title="Choose a store" message="Select a store from the top bar to manage orders." />;
  }

  const orders = ordersQuery.data ?? [];

  return (
    <section className="page-section">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Store operations</p>
          <h1>Orders</h1>
        </div>
      </div>

      {ordersQuery.isLoading ? <StateBlock title="Loading orders" /> : null}
      {ordersQuery.isError ? <StateBlock title="Could not load orders" message="Check ownership for the selected store." /> : null}
      {!ordersQuery.isLoading && orders.length === 0 ? <StateBlock title="No orders yet" message="New client orders will appear here." /> : null}

      {orders.length > 0 ? (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Order</th>
                <th>Status</th>
                <th>Fulfillment</th>
                <th>Payment</th>
                <th>Total</th>
                <th>Created</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {orders.map((order) => (
                <tr key={order.id}>
                  <td>
                    <strong>{order.orderNumber}</strong>
                    <span className="muted block">{order.storeName}</span>
                  </td>
                  <td><span className={`status-pill ${statusClass(order.status)}`}>{order.status.replaceAll('_', ' ')}</span></td>
                  <td>
                    {order.fulfillmentType.replaceAll('_', ' ')}
                    {order.scheduledFor ? <span className="muted block">{formatDate(order.scheduledFor)}</span> : null}
                  </td>
                  <td>{order.paymentStatus.replaceAll('_', ' ')}</td>
                  <td>{formatMoney(order.total)}</td>
                  <td>{formatDate(order.createdAt)}</td>
                  <td>
                    <div className="button-row">
                      <button disabled={order.status !== 'PENDING'} onClick={() => acceptMutation.mutate(order.id)} type="button">Accept</button>
                      <button disabled={order.status !== 'PENDING'} onClick={() => rejectMutation.mutate(order.id)} type="button">Reject</button>
                      <button disabled={order.status !== 'ACCEPTED_BY_STORE'} onClick={() => preparingMutation.mutate(order.id)} type="button">Preparing</button>
                      <button disabled={order.status !== 'PREPARING'} onClick={() => readyMutation.mutate(order.id)} type="button">Ready</button>
                      <button disabled={order.status !== 'READY_FOR_PICKUP'} onClick={() => deliveryMutation.mutate(order.id)} type="button">Request driver</button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : null}
    </section>
  );
}

function statusClass(status: OrderStatus) {
  if (['CANCELLED', 'REJECTED_BY_STORE', 'ISSUE_REPORTED'].includes(status)) {
    return 'danger';
  }
  if (['READY_FOR_PICKUP', 'DELIVERED', 'COMPLETED'].includes(status)) {
    return 'active';
  }
  return 'pending';
}
