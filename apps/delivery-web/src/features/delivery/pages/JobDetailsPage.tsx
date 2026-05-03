import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { formatMoney, formatStatus } from '../../../lib/formatters';
import { acceptJob, getDriverJob } from '../api/deliveryApi';

export function JobDetailsPage() {
  const { deliveryId = '' } = useParams();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const job = useQuery({ queryKey: ['driver-job', deliveryId], queryFn: () => getDriverJob(deliveryId), enabled: Boolean(deliveryId) });
  const acceptMutation = useMutation({
    mutationFn: acceptJob,
    onSuccess() {
      void queryClient.invalidateQueries({ queryKey: ['active-deliveries'] });
      navigate('/active');
    }
  });

  if (!job.data) {
    return <p className="muted">Loading job...</p>;
  }

  return (
    <section className="detail-panel">
      <Link to="/jobs">Back to jobs</Link>
      <h1>{job.data.orderNumber ?? job.data.id}</h1>
      <p>Store: {job.data.storeName ?? 'Store delivery'}</p>
      <p>Status: {formatStatus(job.data.status)}</p>
      <p>Estimated fee: {formatMoney(job.data.deliveryFee)}</p>
      <p>Pickup area: {job.data.pickupAddressSnapshot ?? 'Pending'}</p>
      <p>Dropoff area: {job.data.dropoffAddressSnapshot ?? 'Pending'}</p>
      <p>Cash collection required for COD orders. Full payment details stay backend-controlled.</p>
      <button className="primary-button" onClick={() => acceptMutation.mutate(job.data.id)}>Accept job</button>
    </section>
  );
}
