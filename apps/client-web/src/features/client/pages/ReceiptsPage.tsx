import { useQuery } from '@tanstack/react-query';
import { formatMoney } from '../../../lib/formatters';
import { getReceipts } from '../api/clientApi';

export function ReceiptsPage() {
  const receipts = useQuery({ queryKey: ['client-receipts'], queryFn: getReceipts });

  return (
    <section className="stack">
      <div className="page-header">
        <h1>Receipts</h1>
        <p className="muted">Immutable receipt snapshots for completed payment flows.</p>
      </div>
      {receipts.isLoading ? <p className="muted">Loading receipts...</p> : null}
      <div className="card-grid">
        {receipts.data?.map((receipt) => (
          <article className="card" key={receipt.id}>
            <h2>{receipt.receiptNumber}</h2>
            <p>{receipt.storeName ?? receipt.receiptType}</p>
            <strong>{formatMoney(receipt.totalAmount)}</strong>
          </article>
        ))}
      </div>
    </section>
  );
}
