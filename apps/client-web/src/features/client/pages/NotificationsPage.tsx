import { useQuery } from '@tanstack/react-query';
import { getNotifications } from '../api/clientApi';

export function NotificationsPage() {
  const notifications = useQuery({ queryKey: ['notifications'], queryFn: getNotifications });

  return (
    <section className="stack">
      <div className="page-header">
        <h1>Notifications</h1>
        <p className="muted">Important order, delivery, payment, and support updates.</p>
      </div>
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
