import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { StateBlock } from '../../../components/feedback/StateBlock';
import { formatDate } from '../../../lib/formatters';
import { getNotifications, getUnreadNotificationCount, markAllNotificationsRead, markNotificationRead } from '../api/notificationsApi';

export function NotificationsPage() {
  const queryClient = useQueryClient();
  const notificationsQuery = useQuery({ queryKey: ['admin-notifications'], queryFn: getNotifications });
  const unreadQuery = useQuery({ queryKey: ['admin-notifications-unread-count'], queryFn: getUnreadNotificationCount });
  const invalidate = async () => {
    await queryClient.invalidateQueries({ queryKey: ['admin-notifications'] });
    await queryClient.invalidateQueries({ queryKey: ['admin-notifications-unread-count'] });
  };
  const markReadMutation = useMutation({ mutationFn: markNotificationRead, onSuccess: invalidate });
  const markAllMutation = useMutation({ mutationFn: markAllNotificationsRead, onSuccess: invalidate });
  const notifications = notificationsQuery.data ?? [];

  return (
    <section className="page-section">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Admin updates</p>
          <h1>Notifications</h1>
        </div>
        <button className="primary-button" type="button" onClick={() => markAllMutation.mutate()}>
          Mark all read ({unreadQuery.data?.unreadCount ?? 0})
        </button>
      </div>

      {notificationsQuery.isLoading ? <StateBlock title="Loading notifications" /> : null}
      {notificationsQuery.isError ? <StateBlock title="Could not load notifications" /> : null}
      {!notificationsQuery.isLoading && notifications.length === 0 ? <StateBlock title="No notifications yet" /> : null}

      {notifications.length > 0 ? (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Notification</th>
                <th>Status</th>
                <th>Created</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {notifications.map((notification) => (
                <tr key={notification.id}>
                  <td>
                    <strong>{notification.title}</strong>
                    <span className="muted block">{notification.body}</span>
                  </td>
                  <td>{notification.readAt ? 'Read' : 'Unread'}</td>
                  <td>{formatDate(notification.createdAt)}</td>
                  <td>
                    <button disabled={Boolean(notification.readAt)} onClick={() => markReadMutation.mutate(notification.id)} type="button">Mark read</button>
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
