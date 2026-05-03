import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { formatMoney, formatStatus } from '../../../lib/formatters';
import { acceptJob, getAvailableJobs } from '../api/deliveryApi';

export function JobsPage() {
  const queryClient = useQueryClient();
  const jobs = useQuery({ queryKey: ['available-jobs'], queryFn: getAvailableJobs });
  const acceptMutation = useMutation({
    mutationFn: acceptJob,
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['available-jobs'] });
      void queryClient.invalidateQueries({ queryKey: ['active-deliveries'] });
    }
  });

  return (
    <section className="stack">
      <div className="page-header">
        <h1>Available jobs</h1>
        <p className="muted">Waiting platform-driver deliveries are shown here.</p>
      </div>
      {jobs.isLoading ? <p className="muted">Loading jobs...</p> : null}
      {jobs.isError ? <p className="error-text">Create an approved driver profile first, then try again.</p> : null}
      <div className="card-grid">
        {jobs.data?.map((job) => (
          <article className="card" key={job.id}>
            <h2>{job.storeName ?? 'Store delivery'}</h2>
            <p>Order: {job.orderNumber ?? job.orderId}</p>
            <p>Pickup: {job.pickupAddressSnapshot ?? 'Pending'}</p>
            <p>Dropoff: {job.dropoffAddressSnapshot ?? 'Pending'}</p>
            <p>{formatStatus(job.status)} - {formatMoney(job.deliveryFee)}</p>
            <div className="button-row">
              <Link className="secondary-button" to={`/jobs/${job.id}`}>Details</Link>
              <button className="primary-button" onClick={() => acceptMutation.mutate(job.id)}>Accept job</button>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}
