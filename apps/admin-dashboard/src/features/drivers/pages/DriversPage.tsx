import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { StateBlock } from '../../../components/feedback/StateBlock';
import { formatDate, label } from '../../../lib/formatters';
import { approveDriver, getAdminDrivers, rejectDriver, suspendDriver } from '../../admin/api/adminApi';

export function DriversPage() {
  const queryClient = useQueryClient();
  const driversQuery = useQuery({ queryKey: ['admin-drivers'], queryFn: getAdminDrivers });
  const invalidate = async () => {
    await queryClient.invalidateQueries({ queryKey: ['admin-drivers'] });
  };
  const approveMutation = useMutation({ mutationFn: (driverId: string) => approveDriver(driverId, withReason('Approve driver')), onSuccess: invalidate });
  const rejectMutation = useMutation({ mutationFn: (driverId: string) => rejectDriver(driverId, withReason('Reject driver')), onSuccess: invalidate });
  const suspendMutation = useMutation({ mutationFn: (driverId: string) => suspendDriver(driverId, withReason('Suspend driver')), onSuccess: invalidate });
  const drivers = driversQuery.data ?? [];

  return (
    <section className="page-section">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Delivery actors</p>
          <h1>Drivers</h1>
        </div>
      </div>

      {driversQuery.isLoading ? <StateBlock title="Loading drivers" /> : null}
      {driversQuery.isError ? <StateBlock title="Could not load drivers" /> : null}
      {!driversQuery.isLoading && drivers.length === 0 ? <StateBlock title="No drivers found" /> : null}

      {drivers.length > 0 ? (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Driver</th>
                <th>Type</th>
                <th>Vehicle</th>
                <th>Status</th>
                <th>Phone</th>
                <th>Created</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {drivers.map((driver) => (
                <tr key={driver.id}>
                  <td>
                    <strong>{driver.fullName}</strong>
                    <span className="muted block">{driver.id}</span>
                  </td>
                  <td>{label(driver.driverType)}</td>
                  <td>{driver.vehicleType || '-'}</td>
                  <td><span className={`status-pill ${driver.status.toLowerCase()}`}>{label(driver.status)}</span></td>
                  <td>{driver.phoneNumber || '-'}</td>
                  <td>{formatDate(driver.createdAt)}</td>
                  <td>
                    <div className="button-row">
                      <button disabled={driver.status === 'ACTIVE'} onClick={() => approveMutation.mutate(driver.id)} type="button">Approve</button>
                      <button disabled={driver.status === 'REJECTED'} onClick={() => rejectMutation.mutate(driver.id)} type="button">Reject</button>
                      <button disabled={driver.status === 'SUSPENDED'} onClick={() => suspendMutation.mutate(driver.id)} type="button">Suspend</button>
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
