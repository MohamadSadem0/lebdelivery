import { useQuery } from '@tanstack/react-query';
import { getNotifications } from '../api/deliveryApi';

export function NotificationsPage() {
  const notifications = useQuery({ queryKey: ['notifications'], queryFn: getNotifications });

  return (
    <section className="stack">
      <div className="page-header">
        <h1>Notifications</h1>
        <p className="muted">Driver-facing order, delivery, payment, and support updates.</p>
      </div>
      {notifications.isLoading ? <p className="muted">Loading notifications...</p> : null}
      <div className="stack">
        {notifications.data?.map((notification) => (
          <article className="card" key={notification.id}>
            <h2>{notification.title}</h2>
            <p>{notification.body}</p>
          </article>
        ))}
      </div>
    </section>
  );
}
