import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { StateBlock } from '../../../components/feedback/StateBlock';
import { formatDate, label } from '../../../lib/formatters';
import { activateUser, getAdminUsers, suspendUser } from '../../admin/api/adminApi';

export function UsersPage() {
  const queryClient = useQueryClient();
  const usersQuery = useQuery({ queryKey: ['admin-users'], queryFn: getAdminUsers });
  const invalidate = async () => {
    await queryClient.invalidateQueries({ queryKey: ['admin-users'] });
  };
  const activateMutation = useMutation({ mutationFn: (userId: string) => activateUser(userId, withReason('Activate user')), onSuccess: invalidate });
  const suspendMutation = useMutation({ mutationFn: (userId: string) => suspendUser(userId, withReason('Suspend user')), onSuccess: invalidate });
  const users = usersQuery.data ?? [];

  return (
    <section className="page-section">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Accounts</p>
          <h1>Users</h1>
        </div>
      </div>

      {usersQuery.isLoading ? <StateBlock title="Loading users" /> : null}
      {usersQuery.isError ? <StateBlock title="Could not load users" /> : null}
      {!usersQuery.isLoading && users.length === 0 ? <StateBlock title="No users found" /> : null}

      {users.length > 0 ? (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>User</th>
                <th>Contact</th>
                <th>Status</th>
                <th>Last login</th>
                <th>Created</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.map((user) => (
                <tr key={user.id}>
                  <td>
                    <strong>{user.fullName}</strong>
                    <span className="muted block">{user.id}</span>
                  </td>
                  <td>
                    {user.phone}
                    <span className="muted block">{user.email || '-'}</span>
                  </td>
                  <td><span className={`status-pill ${user.status.toLowerCase()}`}>{label(user.status)}</span></td>
                  <td>{formatDate(user.lastLoginAt)}</td>
                  <td>{formatDate(user.createdAt)}</td>
                  <td>
                    <div className="button-row">
                      <button disabled={user.status === 'ACTIVE'} onClick={() => activateMutation.mutate(user.id)} type="button">Activate</button>
                      <button disabled={user.status === 'SUSPENDED'} onClick={() => suspendMutation.mutate(user.id)} type="button">Suspend</button>
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
