import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { Pressable, ScrollView, StyleSheet, Text, TextInput, View } from 'react-native';
import { Screen } from '../../../components/layout/Screen';
import { PrimaryButton } from '../../../components/ui/PrimaryButton';
import {
  createClientAddress,
  deleteClientAddress,
  getClientAddresses,
  setDefaultClientAddress
} from '../../../lib/addresses';

export function AddressesScreen() {
  const queryClient = useQueryClient();
  const addresses = useQuery({ queryKey: ['client-addresses'], queryFn: getClientAddresses });
  const [label, setLabel] = useState('Home');
  const [fullAddress, setFullAddress] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [instructions, setInstructions] = useState('');

  const invalidate = () => queryClient.invalidateQueries({ queryKey: ['client-addresses'] });

  const createMutation = useMutation({
    mutationFn: createClientAddress,
    onSuccess() {
      setFullAddress('');
      setPhoneNumber('');
      setInstructions('');
      void invalidate();
    }
  });

  const defaultMutation = useMutation({
    mutationFn: setDefaultClientAddress,
    onSuccess: invalidate
  });

  const deleteMutation = useMutation({
    mutationFn: deleteClientAddress,
    onSuccess: invalidate
  });

  const saveAddress = () => {
    if (!label.trim() || !fullAddress.trim()) {
      return;
    }
    createMutation.mutate({
      label,
      fullAddress,
      phoneNumber: phoneNumber || undefined,
      instructions: instructions || undefined,
      defaultAddress: addresses.data?.length === 0
    });
  };

  return (
    <Screen>
      <ScrollView contentContainerStyle={styles.content}>
        <Text style={styles.title}>Saved addresses</Text>
        <Text style={styles.copy}>Use saved addresses at checkout while each order still keeps its own address snapshot.</Text>

        <View style={styles.form}>
          <Text style={styles.label}>Label</Text>
          <TextInput style={styles.input} value={label} onChangeText={setLabel} placeholder="Home, work, parents" />
          <Text style={styles.label}>Full address</Text>
          <TextInput style={styles.input} value={fullAddress} onChangeText={setFullAddress} placeholder="Hamra, Beirut" />
          <Text style={styles.label}>Phone number</Text>
          <TextInput style={styles.input} value={phoneNumber} onChangeText={setPhoneNumber} placeholder="+961..." keyboardType="phone-pad" />
          <Text style={styles.label}>Driver instructions</Text>
          <TextInput
            style={[styles.input, styles.textarea]}
            value={instructions}
            onChangeText={setInstructions}
            placeholder="Building, floor, nearby landmark"
            multiline
          />
          {createMutation.isError ? <Text style={styles.error}>Could not save address.</Text> : null}
          <PrimaryButton onPress={saveAddress}>{createMutation.isPending ? 'Saving...' : 'Save address'}</PrimaryButton>
        </View>

        {addresses.isLoading ? <Text style={styles.copy}>Loading addresses...</Text> : null}
        {addresses.isError ? <Text style={styles.error}>Addresses are unavailable right now.</Text> : null}

        <View style={styles.list}>
          {addresses.data?.map((address) => (
            <View key={address.id} style={styles.card}>
              <View style={styles.row}>
                <Text style={styles.name}>{address.label}</Text>
                {address.defaultAddress ? <Text style={styles.badge}>Default</Text> : null}
              </View>
              <Text style={styles.copy}>{address.fullAddress}</Text>
              {address.phoneNumber ? <Text style={styles.meta}>{address.phoneNumber}</Text> : null}
              {address.instructions ? <Text style={styles.meta}>{address.instructions}</Text> : null}
              <View style={styles.actions}>
                {!address.defaultAddress ? (
                  <Pressable onPress={() => defaultMutation.mutate(address.id)}>
                    <Text style={styles.link}>Set default</Text>
                  </Pressable>
                ) : null}
                <Pressable onPress={() => deleteMutation.mutate(address.id)}>
                  <Text style={styles.danger}>Delete</Text>
                </Pressable>
              </View>
            </View>
          ))}
        </View>
      </ScrollView>
    </Screen>
  );
}

const styles = StyleSheet.create({
  content: { gap: 14, paddingBottom: 28 },
  title: { fontSize: 24, fontWeight: '800', color: '#0f172a' },
  copy: { fontSize: 15, color: '#475569' },
  form: { gap: 8, borderRadius: 8, backgroundColor: '#ffffff', padding: 14, borderWidth: 1, borderColor: '#e2e8f0' },
  label: { fontSize: 14, fontWeight: '700', color: '#334155' },
  input: { minHeight: 46, borderRadius: 8, paddingHorizontal: 12, backgroundColor: '#ffffff', borderWidth: 1, borderColor: '#cbd5e1' },
  textarea: { minHeight: 76, paddingTop: 10, textAlignVertical: 'top' },
  list: { gap: 10 },
  card: { gap: 8, borderRadius: 8, backgroundColor: '#ffffff', padding: 14, borderWidth: 1, borderColor: '#e2e8f0' },
  row: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between', gap: 10 },
  name: { fontSize: 16, fontWeight: '800', color: '#0f172a' },
  badge: { borderRadius: 999, backgroundColor: '#ccfbf1', color: '#115e59', paddingHorizontal: 10, paddingVertical: 4, fontWeight: '800', overflow: 'hidden' },
  meta: { fontSize: 13, color: '#64748b' },
  actions: { flexDirection: 'row', gap: 18, marginTop: 4 },
  link: { color: '#0f766e', fontWeight: '800' },
  danger: { color: '#b91c1c', fontWeight: '800' },
  error: { color: '#b91c1c', fontWeight: '700' }
});
