import { useMutation, useQuery } from '@tanstack/react-query';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import { useEffect, useState } from 'react';
import { Pressable, StyleSheet, Switch, Text, TextInput, View } from 'react-native';
import { Screen } from '../../../components/layout/Screen';
import { PrimaryButton } from '../../../components/ui/PrimaryButton';
import type { RootStackParamList } from '../../../app/navigation/types';
import { getClientAddresses } from '../../../lib/addresses';
import { createOrder } from '../../../lib/orders';
import { useCartStore } from '../../cart/store/cartStore';

type Props = NativeStackScreenProps<RootStackParamList, 'Checkout'>;

export function CheckoutScreen({ navigation }: Props) {
  const { items, storeId, clear, subtotal } = useCartStore();
  const addresses = useQuery({ queryKey: ['client-addresses'], queryFn: getClientAddresses });
  const [selectedAddressId, setSelectedAddressId] = useState<string | undefined>();
  const [address, setAddress] = useState('Hamra, Beirut');
  const [notes, setNotes] = useState('');
  const [needsChange, setNeedsChange] = useState(false);
  const [cashAmountClientHas, setCashAmountClientHas] = useState('');
  const [scheduled, setScheduled] = useState(false);
  const [scheduledFor, setScheduledFor] = useState('');

  const mutation = useMutation({
    mutationFn: createOrder,
    onSuccess(order) {
      clear();
      navigation.replace('OrderTracking', { orderId: order.id });
    }
  });

  useEffect(() => {
    if (!selectedAddressId && addresses.data && addresses.data.length > 0) {
      setSelectedAddressId(addresses.data.find((savedAddress) => savedAddress.defaultAddress)?.id ?? addresses.data[0].id);
    }
  }, [addresses.data, selectedAddressId]);

  const placeOrder = () => {
    if (!storeId || items.length === 0) {
      return;
    }

    mutation.mutate({
      storeId,
      paymentMethod: 'CASH_ON_DELIVERY',
      fulfillmentType: scheduled ? 'SCHEDULED' : 'NOW',
      scheduledFor: scheduled && scheduledFor ? new Date(scheduledFor).toISOString() : undefined,
      needsChange,
      cashAmountClientHas: cashAmountClientHas || undefined,
      notes,
      addressId: selectedAddressId,
      address: selectedAddressId
        ? undefined
        : {
            label: 'Manual',
            fullAddress: address
          },
      items: items.map((item) => ({
        productId: item.productId,
        productVariantId: item.productVariantId,
        quantity: item.quantity
      }))
    });
  };

  return (
    <Screen>
      <Text style={styles.title}>Checkout</Text>
      {items.map((item) => (
        <Text key={`${item.productId}-${item.productVariantId ?? 'base'}`} style={styles.copy}>
          {item.name} x {item.quantity}
        </Text>
      ))}
      <Text style={styles.total}>Subtotal estimate: {subtotal().toFixed(2)}</Text>
      <View style={styles.switchRow}>
        <Text style={styles.label}>Schedule for later</Text>
        <Switch value={scheduled} onValueChange={setScheduled} />
      </View>
      {scheduled ? (
        <>
          <Text style={styles.label}>Scheduled time</Text>
          <TextInput
            style={styles.input}
            value={scheduledFor}
            onChangeText={setScheduledFor}
            placeholder="2026-05-03T18:30:00"
          />
          <Text style={styles.hint}>Use a time at least 30 minutes from now and within 14 days.</Text>
        </>
      ) : null}
      <Text style={styles.label}>Saved address</Text>
      {addresses.isLoading ? <Text style={styles.copy}>Loading saved addresses...</Text> : null}
      {addresses.data?.map((savedAddress) => (
        <Pressable
          key={savedAddress.id}
          style={[styles.addressCard, selectedAddressId === savedAddress.id && styles.addressCardSelected]}
          onPress={() => setSelectedAddressId(savedAddress.id)}
        >
          <Text style={styles.addressName}>{savedAddress.label}{savedAddress.defaultAddress ? ' - Default' : ''}</Text>
          <Text style={styles.copy}>{savedAddress.fullAddress}</Text>
        </Pressable>
      ))}
      {addresses.data && addresses.data.length > 0 ? (
        <Pressable onPress={() => setSelectedAddressId(undefined)}>
          <Text style={styles.link}>Use a different address for this order</Text>
        </Pressable>
      ) : null}
      {!selectedAddressId ? (
        <>
          <Text style={styles.label}>Manual address</Text>
          <TextInput style={styles.input} value={address} onChangeText={setAddress} />
        </>
      ) : null}
      <Text style={styles.label}>Notes</Text>
      <TextInput style={styles.input} value={notes} onChangeText={setNotes} placeholder="Call before arriving" />
      <View style={styles.switchRow}>
        <Text style={styles.label}>Need change for cash</Text>
        <Switch value={needsChange} onValueChange={setNeedsChange} />
      </View>
      <TextInput
        style={styles.input}
        value={cashAmountClientHas}
        onChangeText={setCashAmountClientHas}
        placeholder="Cash amount client has"
        keyboardType="decimal-pad"
      />
      {mutation.isError ? <Text style={styles.error}>Could not place order. Check stock and try again.</Text> : null}
      <PrimaryButton onPress={placeOrder}>{mutation.isPending ? 'Placing order...' : 'Place order'}</PrimaryButton>
    </Screen>
  );
}

const styles = StyleSheet.create({
  title: { fontSize: 24, fontWeight: '800', color: '#0f172a' },
  copy: { marginTop: 8, fontSize: 16, color: '#475569' },
  total: { marginVertical: 16, fontSize: 18, fontWeight: '800', color: '#0f172a' },
  label: { marginTop: 12, fontSize: 14, fontWeight: '700', color: '#334155' },
  input: { minHeight: 48, borderRadius: 8, paddingHorizontal: 12, backgroundColor: '#ffffff', borderWidth: 1, borderColor: '#cbd5e1', marginTop: 6 },
  addressCard: { marginTop: 8, borderRadius: 8, backgroundColor: '#ffffff', padding: 12, borderWidth: 1, borderColor: '#e2e8f0' },
  addressCardSelected: { borderColor: '#0f766e', backgroundColor: '#f0fdfa' },
  addressName: { fontSize: 15, fontWeight: '800', color: '#0f172a' },
  link: { marginTop: 10, color: '#0f766e', fontWeight: '800' },
  hint: { marginTop: 6, fontSize: 13, color: '#64748b' },
  switchRow: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between' },
  error: { color: '#b91c1c', fontWeight: '600', marginVertical: 10 }
});
