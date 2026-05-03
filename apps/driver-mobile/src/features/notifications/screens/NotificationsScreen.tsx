import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { StyleSheet, Text, View } from 'react-native';
import { Screen } from '../../../components/layout/Screen';
import { PrimaryButton } from '../../../components/ui/PrimaryButton';
import { getNotifications, markAllNotificationsRead, markNotificationRead } from '../../../lib/notifications';

export function NotificationsScreen() {
  const queryClient = useQueryClient();
  const notificationsQuery = useQuery({ queryKey: ['driver-notifications'], queryFn: getNotifications });
  const markReadMutation = useMutation({
    mutationFn: markNotificationRead,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['driver-notifications'] })
  });
  const markAllMutation = useMutation({
    mutationFn: markAllNotificationsRead,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['driver-notifications'] })
  });

  return (
    <Screen>
      <View style={styles.header}>
        <Text style={styles.title}>Notifications</Text>
        <PrimaryButton onPress={() => markAllMutation.mutate()}>Mark all read</PrimaryButton>
      </View>
      {notificationsQuery.isLoading ? <Text style={styles.copy}>Loading notifications...</Text> : null}
      {notificationsQuery.isError ? <Text style={styles.error}>Notifications could not be loaded.</Text> : null}
      {notificationsQuery.data?.length === 0 ? <Text style={styles.copy}>No notifications yet.</Text> : null}
      {notificationsQuery.data?.map((notification) => (
        <View key={notification.id} style={[styles.item, !notification.readAt && styles.unread]}>
          <Text style={styles.itemTitle}>{notification.title}</Text>
          <Text style={styles.copy}>{notification.body}</Text>
          <Text style={styles.meta}>{new Date(notification.createdAt).toLocaleString()}</Text>
          {!notification.readAt ? <PrimaryButton onPress={() => markReadMutation.mutate(notification.id)}>Mark read</PrimaryButton> : null}
        </View>
      ))}
    </Screen>
  );
}

const styles = StyleSheet.create({
  header: { gap: 10, marginBottom: 12 },
  title: { fontSize: 24, fontWeight: '800', color: '#0f172a' },
  item: { borderRadius: 8, backgroundColor: '#f1f5f9', padding: 12, marginTop: 10, gap: 6 },
  unread: { backgroundColor: '#dcfce7' },
  itemTitle: { fontSize: 16, fontWeight: '800', color: '#0f172a' },
  copy: { fontSize: 15, color: '#475569' },
  meta: { fontSize: 12, color: '#64748b', fontWeight: '600' },
  error: { marginTop: 8, fontSize: 16, color: '#b91c1c', fontWeight: '700' }
});
