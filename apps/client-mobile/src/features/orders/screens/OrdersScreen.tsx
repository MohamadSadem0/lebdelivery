import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import { Pressable, StyleSheet, Text, View } from 'react-native';
import { Screen } from '../../../components/layout/Screen';
import type { RootStackParamList } from '../../../app/navigation/types';
import { getClientOrders, reorderOrder } from '../../../lib/orders';

type Props = NativeStackScreenProps<RootStackParamList, 'Orders'>;

export function OrdersScreen({ navigation }: Props) {
  const queryClient = useQueryClient();
  const orders = useQuery({ queryKey: ['client-orders'], queryFn: getClientOrders });
  const reorderMutation = useMutation({
    mutationFn: reorderOrder,
    onSuccess(order) {
      void queryClient.invalidateQueries({ queryKey: ['client-orders'] });
      navigation.navigate('OrderTracking', { orderId: order.id });
    }
  });

  return (
    <Screen>
      <Text style={styles.title}>Orders</Text>
      {orders.isLoading ? <Text style={styles.copy}>Loading orders...</Text> : null}
      {orders.isError ? <Text style={styles.copy}>Orders are unavailable right now.</Text> : null}
      {reorderMutation.isError ? <Text style={styles.error}>Could not reorder. The store, products, or stock may have changed.</Text> : null}
      <View style={styles.list}>
        {orders.data?.map((order) => (
          <Pressable key={order.id} style={styles.item} onPress={() => navigation.navigate('OrderTracking', { orderId: order.id })}>
            <Text style={styles.name}>{order.orderNumber}</Text>
            <Text style={styles.copy}>{order.storeName} - {order.status} - {order.total}</Text>
            <Text style={styles.copy}>{order.fulfillmentType === 'SCHEDULED' ? `Scheduled: ${order.scheduledFor}` : 'Order now'}</Text>
            <Pressable style={styles.reorderButton} onPress={() => reorderMutation.mutate(order.id)}>
              <Text style={styles.reorderText}>{reorderMutation.isPending ? 'Checking...' : 'Reorder'}</Text>
            </Pressable>
          </Pressable>
        ))}
      </View>
    </Screen>
  );
}

const styles = StyleSheet.create({
  title: { fontSize: 24, fontWeight: '800', color: '#0f172a' },
  copy: { marginTop: 8, fontSize: 16, color: '#475569' },
  error: { marginTop: 8, color: '#b91c1c', fontWeight: '700' },
  list: { marginTop: 16, gap: 10 },
  item: { borderRadius: 8, backgroundColor: '#ffffff', padding: 14, borderWidth: 1, borderColor: '#e2e8f0' },
  name: { fontSize: 16, fontWeight: '800', color: '#0f172a' },
  reorderButton: { marginTop: 10, alignSelf: 'flex-start', borderRadius: 8, backgroundColor: '#0f766e', paddingHorizontal: 12, paddingVertical: 8 },
  reorderText: { color: '#ffffff', fontWeight: '800' }
});
