import { useQuery } from '@tanstack/react-query';
import { formatMoney } from '../../../lib/formatters';
import { getDriverEarnings } from '../api/deliveryApi';

export function EarningsPage() {
  const earnings = useQuery({ queryKey: ['driver-earnings'], queryFn: getDriverEarnings });

  return (
    <section className="stack">
      <div className="page-header">
        <h1>Earnings</h1>
        <p className="muted">MVP totals use delivered delivery fees and collected COD records.</p>
      </div>
      {earnings.isLoading ? <p className="muted">Loading earnings...</p> : null}
      {earnings.isError ? <p className="error-text">Earnings are unavailable. Create an active driver profile first.</p> : null}
      <div className="metric-grid">
        <article className="metric-card">
          <span>Completed deliveries</span>
          <strong>{earnings.data?.completedDeliveries ?? 0}</strong>
        </article>
        <article className="metric-card">
          <span>Delivery fees</span>
          <strong>{formatMoney(earnings.data?.totalDeliveryFees)}</strong>
        </article>
        <article className="metric-card">
          <span>Settled cash</span>
          <strong>{formatMoney(earnings.data?.settledCashTotal)}</strong>
        </article>
        <article className="metric-card">
          <span>Unsettled cash</span>
          <strong>{formatMoney(earnings.data?.unsettledCashTotal)}</strong>
        </article>
      </div>
      {earnings.data?.note ? <article className="card"><p>{earnings.data.note}</p></article> : null}
    </section>
  );
}
