import { useQuery } from '@tanstack/react-query';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import { StyleSheet, Text, View } from 'react-native';
import { Screen } from '../../../components/layout/Screen';
import type { RootStackParamList } from '../../../app/navigation/types';
import { getClientOrder, getOrderTimeline } from '../../../lib/orders';

type Props = NativeStackScreenProps<RootStackParamList, 'OrderTracking'>;

export function OrderTrackingScreen({ route }: Props) {
  const order = useQuery({
    queryKey: ['client-order', route.params.orderId],
    queryFn: () => getClientOrder(route.params.orderId)
  });
  const timeline = useQuery({
    queryKey: ['client-order-timeline', route.params.orderId],
    queryFn: () => getOrderTimeline(route.params.orderId)
  });

  return (
    <Screen>
      <Text style={styles.title}>{order.data?.orderNumber ?? 'Order tracking'}</Text>
      {order.data ? <Text style={styles.copy}>{order.data.storeName} · {order.data.status}</Text> : null}
      <View style={styles.timeline}>
        {timeline.data?.events.map((event) => (
          <View key={event.id} style={styles.event}>
            <Text style={styles.name}>{event.title}</Text>
            <Text style={styles.copy}>{event.status}</Text>
            {event.description ? <Text style={styles.copy}>{event.description}</Text> : null}
          </View>
        ))}
      </View>
    </Screen>
  );
}

const styles = StyleSheet.create({
  title: { fontSize: 24, fontWeight: '800', color: '#0f172a' },
  copy: { marginTop: 8, fontSize: 16, color: '#475569' },
  timeline: { marginTop: 16, gap: 10 },
  event: { borderRadius: 8, backgroundColor: '#ffffff', padding: 14, borderWidth: 1, borderColor: '#e2e8f0' },
  name: { fontSize: 16, fontWeight: '800', color: '#0f172a' }
});
