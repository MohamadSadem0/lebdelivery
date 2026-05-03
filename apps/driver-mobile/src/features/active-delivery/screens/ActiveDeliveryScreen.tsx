import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { StyleSheet, Text, TextInput, View } from 'react-native';
import { Screen } from '../../../components/layout/Screen';
import { PrimaryButton } from '../../../components/ui/PrimaryButton';
import type { RootStackParamList } from '../../../app/navigation/types';
import { getActiveDeliveries, markCashCollected, updateDeliveryStatus } from '../../../lib/deliveries';

type Props = NativeStackScreenProps<RootStackParamList, 'ActiveDelivery'>;

export function ActiveDeliveryScreen({ route }: Props) {
  const queryClient = useQueryClient();
  const [collectedAmount, setCollectedAmount] = useState('');
  const activeQuery = useQuery({ queryKey: ['active-deliveries'], queryFn: getActiveDeliveries });
  const selected = activeQuery.data?.find((delivery) => delivery.id === route.params?.deliveryId) ?? activeQuery.data?.[0];
  const statusMutation = useMutation({
    mutationFn: (action: 'arrived-pickup' | 'picked-up' | 'on-the-way' | 'arrived-destination' | 'delivered') =>
      selected ? updateDeliveryStatus(selected.id, action) : Promise.reject(new Error('No delivery selected')),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['active-deliveries'] })
  });
  const cashMutation = useMutation({
    mutationFn: () =>
      selected
        ? markCashCollected(selected.id, {
            collectedAmount: collectedAmount || '0',
            note: 'Collected from driver app MVP'
          })
        : Promise.reject(new Error('No delivery selected')),
    onSuccess: () => {
      setCollectedAmount('');
      queryClient.invalidateQueries({ queryKey: ['active-deliveries'] });
    }
  });

  return (
    <Screen>
      <View style={styles.content}>
        <Text style={styles.title}>Active delivery</Text>
        {activeQuery.isLoading ? <Text style={styles.copy}>Loading active delivery...</Text> : null}
        {activeQuery.isError ? <Text style={styles.error}>Create a driver profile first, then try again.</Text> : null}
        {!selected && !activeQuery.isLoading ? <Text style={styles.copy}>No active delivery.</Text> : null}
        {selected ? (
          <>
            <Text style={styles.copy}>Order: {selected.orderNumber}</Text>
            <Text style={styles.copy}>Pickup: {selected.pickupAddressSnapshot ?? 'Pending'}</Text>
            <Text style={styles.copy}>Dropoff: {selected.dropoffAddressSnapshot ?? 'Pending'}</Text>
            <Text style={styles.copy}>Status: {selected.status}</Text>
            <PrimaryButton onPress={() => statusMutation.mutate('arrived-pickup')}>Arrived at pickup</PrimaryButton>
            <PrimaryButton onPress={() => statusMutation.mutate('picked-up')}>Picked up</PrimaryButton>
            <PrimaryButton onPress={() => statusMutation.mutate('on-the-way')}>On the way</PrimaryButton>
            <PrimaryButton onPress={() => statusMutation.mutate('arrived-destination')}>Arrived at destination</PrimaryButton>
            <PrimaryButton onPress={() => statusMutation.mutate('delivered')}>Delivered</PrimaryButton>
            {statusMutation.isError ? <Text style={styles.error}>Status update failed. Check the current status order.</Text> : null}
            {selected.status === 'DELIVERED' ? (
              <>
                <Text style={styles.sectionTitle}>Cash collection</Text>
                <TextInput
                  style={styles.input}
                  value={collectedAmount}
                  onChangeText={setCollectedAmount}
                  keyboardType="decimal-pad"
                  placeholder="Collected amount"
                />
                <PrimaryButton onPress={() => cashMutation.mutate()}>Mark cash collected</PrimaryButton>
                {cashMutation.isError ? <Text style={styles.error}>Cash collection failed. Check amount and payment status.</Text> : null}
                {cashMutation.isSuccess ? <Text style={styles.success}>Cash collection recorded and receipt generated.</Text> : null}
              </>
            ) : null}
          </>
        ) : null}
      </View>
    </Screen>
  );
}

const styles = StyleSheet.create({
  content: { flex: 1, gap: 10 },
  title: {
    fontSize: 24,
    fontWeight: '800',
    color: '#0f172a'
  },
  copy: {
    marginTop: 8,
    fontSize: 16,
    color: '#475569'
  },
  sectionTitle: { marginTop: 14, fontSize: 18, fontWeight: '800', color: '#0f172a' },
  input: {
    borderWidth: 1,
    borderColor: '#cbd5e1',
    borderRadius: 8,
    paddingHorizontal: 12,
    paddingVertical: 10,
    fontSize: 16
  },
  error: { color: '#b91c1c', fontWeight: '700' },
  success: { color: '#047857', fontWeight: '700' }
});
