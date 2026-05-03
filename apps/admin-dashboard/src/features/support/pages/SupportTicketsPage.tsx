import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import type { SupportTicketStatus } from '@lebanon-platform/shared-types';
import { StateBlock } from '../../../components/feedback/StateBlock';
import { formatDate, label } from '../../../lib/formatters';
import { getAdminSupportTickets, updateSupportTicketStatus } from '../../admin/api/adminApi';

const statuses: SupportTicketStatus[] = ['OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED'];

export function SupportTicketsPage() {
  const queryClient = useQueryClient();
  const ticketsQuery = useQuery({ queryKey: ['admin-support-tickets'], queryFn: getAdminSupportTickets });
  const updateMutation = useMutation({
    mutationFn: ({ ticketId, status }: { ticketId: string; status: SupportTicketStatus }) =>
      updateSupportTicketStatus(ticketId, status, window.prompt('Support note', 'Reviewed by support') || undefined),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['admin-support-tickets'] })
  });
  const tickets = ticketsQuery.data ?? [];

  return (
    <section className="page-section">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Issue handling</p>
          <h1>Support Tickets</h1>
        </div>
      </div>

      {ticketsQuery.isLoading ? <StateBlock title="Loading support tickets" /> : null}
      {ticketsQuery.isError ? <StateBlock title="Could not load support tickets" /> : null}
      {!ticketsQuery.isLoading && tickets.length === 0 ? <StateBlock title="No support tickets found" /> : null}

      {tickets.length > 0 ? (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Ticket</th>
                <th>User</th>
                <th>Status</th>
                <th>Related</th>
                <th>Created</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {tickets.map((ticket) => (
                <tr key={ticket.id}>
                  <td>
                    <strong>{ticket.subject}</strong>
                    <span className="muted block">{ticket.description}</span>
                    {ticket.adminNote ? <span className="muted block">Note: {ticket.adminNote}</span> : null}
                  </td>
                  <td>
                    {ticket.userName || ticket.userId}
                    <span className="muted block">{ticket.userId}</span>
                  </td>
                  <td><span className={`status-pill ${ticket.status.toLowerCase()}`}>{label(ticket.status)}</span></td>
                  <td>
                    {ticket.relatedOrderId ? <span className="block">Order {ticket.relatedOrderId}</span> : null}
                    {ticket.relatedDeliveryId ? <span className="block">Delivery {ticket.relatedDeliveryId}</span> : null}
                    {ticket.relatedServiceRequestId ? <span className="block">Service {ticket.relatedServiceRequestId}</span> : null}
                    {!ticket.relatedOrderId && !ticket.relatedDeliveryId && !ticket.relatedServiceRequestId ? '-' : null}
                  </td>
                  <td>{formatDate(ticket.createdAt)}</td>
                  <td>
                    <div className="button-row">
                      {statuses.map((status) => (
                        <button key={status} disabled={ticket.status === status} onClick={() => updateMutation.mutate({ ticketId: ticket.id, status })} type="button">
                          {label(status)}
                        </button>
                      ))}
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
