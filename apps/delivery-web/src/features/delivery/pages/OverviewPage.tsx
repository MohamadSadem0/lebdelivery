import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { formatMoney, formatStatus } from '../../../lib/formatters';
import { getActiveDeliveries, getAvailableJobs, getDriverProfile } from '../api/deliveryApi';

export function OverviewPage() {
  const profile = useQuery({ queryKey: ['driver-profile'], queryFn: getDriverProfile });
  const jobs = useQuery({ queryKey: ['available-jobs'], queryFn: getAvailableJobs });
  const active = useQuery({ queryKey: ['active-deliveries'], queryFn: getActiveDeliveries });

  return (
    <section className="stack">
      <div className="page-header">
        <p className="eyebrow">Driver workspace</p>
        <h1>Delivery operations</h1>
        <p className="muted">Web companion for job review, delivery status updates, and COD collection.</p>
      </div>
      <div className="metric-grid">
        <article className="metric-card">
          <span>Profile</span>
          <strong>{profile.data ? formatStatus(profile.data.status) : 'not ready'}</strong>
        </article>
        <article className="metric-card">
          <span>Available jobs</span>
          <strong>{jobs.data?.length ?? 0}</strong>
        </article>
        <article className="metric-card">
          <span>Active deliveries</span>
          <strong>{active.data?.length ?? 0}</strong>
        </article>
      </div>
      <div className="card-grid">
        {active.data?.map((delivery) => (
          <Link className="card" key={delivery.id} to={`/active?deliveryId=${delivery.id}`}>
            <h2>{delivery.orderNumber ?? delivery.id}</h2>
            <p>{delivery.storeName ?? 'Store delivery'}</p>
            <p>{formatStatus(delivery.status)} - {formatMoney(delivery.deliveryFee)}</p>
          </Link>
        ))}
      </div>
    </section>
  );
}
