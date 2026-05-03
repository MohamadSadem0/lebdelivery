import { useQuery } from '@tanstack/react-query';
import { StateBlock } from '../../../components/feedback/StateBlock';
import { formatDate, formatMoney } from '../../../lib/formatters';
import { useStoreSelectionStore } from '../../stores/store/storeSelectionStore';
import { getStoreReceipts } from '../api/receiptsApi';

export function ReceiptsPage() {
  const selectedStoreId = useStoreSelectionStore((state) => state.selectedStoreId);
  const receiptsQuery = useQuery({
    queryKey: ['store-receipts', selectedStoreId],
    queryFn: () => getStoreReceipts(selectedStoreId as string),
    enabled: Boolean(selectedStoreId)
  });

  if (!selectedStoreId) {
    return <StateBlock title="Choose a store" message="Select a store from the top bar to view receipts." />;
  }

  const receipts = receiptsQuery.data ?? [];

  return (
    <section className="page-section">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Receipts</p>
          <h1>Store Receipts</h1>
        </div>
      </div>

      {receiptsQuery.isLoading ? <StateBlock title="Loading receipts" /> : null}
      {receiptsQuery.isError ? <StateBlock title="Could not load receipts" /> : null}
      {!receiptsQuery.isLoading && receipts.length === 0 ? <StateBlock title="No receipts yet" message="Completed COD orders generate receipts after cash collection." /> : null}

      {receipts.length > 0 ? (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Receipt</th>
                <th>Order</th>
                <th>Client</th>
                <th>Payment</th>
                <th>Total</th>
                <th>Created</th>
              </tr>
            </thead>
            <tbody>
              {receipts.map((receipt) => (
                <tr key={receipt.id}>
                  <td>{receipt.receiptNumber}</td>
                  <td>{receipt.orderNumber || '-'}</td>
                  <td>{receipt.clientName || '-'}</td>
                  <td>{receipt.paymentStatus?.replaceAll('_', ' ') || '-'}</td>
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
