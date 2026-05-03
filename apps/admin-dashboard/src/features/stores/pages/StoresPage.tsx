import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { StateBlock } from '../../../components/feedback/StateBlock';
import { formatDate, label } from '../../../lib/formatters';
import { approveStore, getAdminStores, rejectStore, suspendStore } from '../../admin/api/adminApi';

export function StoresPage() {
  const queryClient = useQueryClient();
  const storesQuery = useQuery({ queryKey: ['admin-stores'], queryFn: getAdminStores });
  const invalidate = async () => {
    await queryClient.invalidateQueries({ queryKey: ['admin-stores'] });
  };
  const approveMutation = useMutation({ mutationFn: (storeId: string) => approveStore(storeId, withReason('Approve store')), onSuccess: invalidate });
  const rejectMutation = useMutation({ mutationFn: (storeId: string) => rejectStore(storeId, withReason('Reject store')), onSuccess: invalidate });
  const suspendMutation = useMutation({ mutationFn: (storeId: string) => suspendStore(storeId, withReason('Suspend store')), onSuccess: invalidate });
  const stores = storesQuery.data ?? [];

  return (
    <section className="page-section">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Approvals</p>
          <h1>Stores</h1>
        </div>
      </div>

      {storesQuery.isLoading ? <StateBlock title="Loading stores" /> : null}
      {storesQuery.isError ? <StateBlock title="Could not load stores" /> : null}
      {!storesQuery.isLoading && stores.length === 0 ? <StateBlock title="No stores found" /> : null}

      {stores.length > 0 ? (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Store</th>
                <th>Owner</th>
                <th>Type</th>
                <th>Status</th>
                <th>Delivery</th>
                <th>Created</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {stores.map((store) => (
                <tr key={store.id}>
                  <td>
                    <strong>{store.name}</strong>
                    <span className="muted block">{store.address || store.id}</span>
                  </td>
                  <td>{store.ownerUserId || '-'}</td>
                  <td>{label(store.storeTypeCode)}</td>
                  <td><span className={`status-pill ${store.status.toLowerCase()}`}>{label(store.status)}</span></td>
                  <td>{label(store.deliveryMode)}</td>
                  <td>{formatDate(store.createdAt)}</td>
                  <td>
                    <div className="button-row">
                      <button disabled={store.status === 'ACTIVE'} onClick={() => approveMutation.mutate(store.id)} type="button">Approve</button>
                      <button disabled={store.status === 'REJECTED'} onClick={() => rejectMutation.mutate(store.id)} type="button">Reject</button>
                      <button disabled={store.status === 'SUSPENDED'} onClick={() => suspendMutation.mutate(store.id)} type="button">Suspend</button>
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

function withReason(action: string) {
  return { reason: window.prompt(`${action} reason`, 'Admin review') || 'Admin review' };
}
