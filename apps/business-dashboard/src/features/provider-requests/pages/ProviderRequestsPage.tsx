import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { StateBlock } from '../../../components/feedback/StateBlock';
import { formatDate, formatMoney } from '../../../lib/formatters';
import { useProviderSelectionStore } from '../../providers/store/providerSelectionStore';
import {
  acceptServiceRequest,
  getProviderServiceRequests,
  markServiceRequestCompleted,
  markServiceRequestInProgress,
  markServiceRequestReady,
  quoteServiceRequest,
  rejectServiceRequest
} from '../api/providerRequestsApi';

export function ProviderRequestsPage() {
  const selectedProviderId = useProviderSelectionStore((state) => state.selectedProviderId);
  const queryClient = useQueryClient();
  const requestsQuery = useQuery({
    queryKey: ['provider-service-requests', selectedProviderId],
    queryFn: () => getProviderServiceRequests(selectedProviderId as string),
    enabled: Boolean(selectedProviderId)
  });
  const invalidate = async () => {
    await queryClient.invalidateQueries({ queryKey: ['provider-service-requests', selectedProviderId] });
  };
  const acceptMutation = useMutation({ mutationFn: (requestId: string) => acceptServiceRequest(selectedProviderId as string, requestId), onSuccess: invalidate });
  const rejectMutation = useMutation({
    mutationFn: (requestId: string) => rejectServiceRequest(selectedProviderId as string, requestId, window.prompt('Reject reason', 'Not available') || 'Not available'),
    onSuccess: invalidate
  });
  const quoteMutation = useMutation({
    mutationFn: (requestId: string) => quoteServiceRequest(selectedProviderId as string, requestId, { quotedAmount: window.prompt('Quote amount', '10.00') || '10.00' }),
    onSuccess: invalidate
  });
  const progressMutation = useMutation({ mutationFn: (requestId: string) => markServiceRequestInProgress(selectedProviderId as string, requestId), onSuccess: invalidate });
  const readyMutation = useMutation({ mutationFn: (requestId: string) => markServiceRequestReady(selectedProviderId as string, requestId), onSuccess: invalidate });
  const completedMutation = useMutation({ mutationFn: (requestId: string) => markServiceRequestCompleted(selectedProviderId as string, requestId), onSuccess: invalidate });

  if (!selectedProviderId) {
    return <StateBlock title="Choose a provider" message="Select a provider before viewing service requests." />;
  }

  const requests = requestsQuery.data ?? [];

  return (
    <section className="page-section">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Provider work</p>
          <h1>Service Requests</h1>
        </div>
      </div>

      {requestsQuery.isLoading ? <StateBlock title="Loading service requests" /> : null}
      {requestsQuery.isError ? <StateBlock title="Could not load service requests" /> : null}
      {!requestsQuery.isLoading && requests.length === 0 ? <StateBlock title="No service requests yet" /> : null}

      {requests.length > 0 ? (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Request</th>
                <th>Client</th>
                <th>Status</th>
                <th>Quote</th>
                <th>Created</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {requests.map((request) => (
                <tr key={request.id}>
                  <td>
                    <strong>{request.serviceName || 'Custom request'}</strong>
                    <span className="muted block">{request.description || request.id}</span>
                  </td>
                  <td>{request.clientName || request.clientUserId || '-'}</td>
                  <td><span className="status-pill pending">{request.status.replaceAll('_', ' ')}</span></td>
                  <td>{formatMoney(request.quotedAmount)}</td>
                  <td>{formatDate(request.createdAt)}</td>
                  <td>
                    <div className="button-row">
                      <button disabled={!['PENDING', 'WAITING_FOR_QUOTE'].includes(request.status)} onClick={() => acceptMutation.mutate(request.id)} type="button">Accept</button>
                      <button disabled={!['PENDING', 'WAITING_FOR_QUOTE'].includes(request.status)} onClick={() => quoteMutation.mutate(request.id)} type="button">Quote</button>
                      <button disabled={['REJECTED', 'CANCELLED', 'COMPLETED'].includes(request.status)} onClick={() => rejectMutation.mutate(request.id)} type="button">Reject</button>
                      <button disabled={!['ACCEPTED', 'QUOTE_ACCEPTED'].includes(request.status)} onClick={() => progressMutation.mutate(request.id)} type="button">In progress</button>
                      <button disabled={request.status !== 'IN_PROGRESS'} onClick={() => readyMutation.mutate(request.id)} type="button">Ready</button>
                      <button disabled={!['IN_PROGRESS', 'READY'].includes(request.status)} onClick={() => completedMutation.mutate(request.id)} type="button">Complete</button>
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
