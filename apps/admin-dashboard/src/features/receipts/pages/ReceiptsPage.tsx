import { useQuery } from '@tanstack/react-query';
import { StateBlock } from '../../../components/feedback/StateBlock';
import { formatDate, formatMoney, label } from '../../../lib/formatters';
import { getAdminReceipts } from '../../admin/api/adminApi';

export function ReceiptsPage() {
  const receiptsQuery = useQuery({ queryKey: ['admin-receipts'], queryFn: getAdminReceipts });
  const receipts = receiptsQuery.data ?? [];

  return (
    <section className="page-section">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Financial records</p>
          <h1>Receipts</h1>
        </div>
      </div>

      {receiptsQuery.isLoading ? <StateBlock title="Loading receipts" /> : null}
      {receiptsQuery.isError ? <StateBlock title="Could not load receipts" /> : null}
      {!receiptsQuery.isLoading && receipts.length === 0 ? <StateBlock title="No receipts found" /> : null}

      {receipts.length > 0 ? (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Receipt</th>
                <th>Order</th>
                <th>Store</th>
                <th>Client</th>
                <th>Payment</th>
                <th>Total</th>
                <th>Created</th>
              </tr>
            </thead>
            <tbody>
              {receipts.map((receipt) => (
                <tr key={receipt.id}>
                  <td>
                    <strong>{receipt.receiptNumber}</strong>
                    <span className="muted block">{label(receipt.receiptType)}</span>
                  </td>
                  <td>{receipt.orderNumber || '-'}</td>
                  <td>{receipt.storeName || receipt.storeId || '-'}</td>
                  <td>{receipt.clientName || receipt.clientUserId || '-'}</td>
                  <td>{label(receipt.paymentStatus)}</td>
                  <td>{formatMoney(receipt.totalAmount)}</td>
                  <td>{formatDate(receipt.createdAt)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : null}
    </section>
  );
}
