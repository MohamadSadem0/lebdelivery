import { useQuery } from '@tanstack/react-query';
import { StyleSheet, Text } from 'react-native';
import { Screen } from '../../../components/layout/Screen';
import { getClientReceipts } from '../../../lib/receipts';

export function ReceiptsScreen() {
  const receiptsQuery = useQuery({ queryKey: ['client-receipts'], queryFn: getClientReceipts });

  return (
    <Screen>
      <Text style={styles.title}>Receipts</Text>
      {receiptsQuery.isLoading ? <Text style={styles.copy}>Loading receipts...</Text> : null}
      {receiptsQuery.isError ? <Text style={styles.error}>Receipts could not be loaded.</Text> : null}
      {receiptsQuery.data?.length === 0 ? <Text style={styles.copy}>No receipts yet.</Text> : null}
      {receiptsQuery.data?.map((receipt) => (
        <Text key={receipt.id} style={styles.copy}>
          {receipt.receiptNumber} - {receipt.storeName ?? 'Store'} - {receipt.totalAmount}
        </Text>
      ))}
    </Screen>
  );
}

const styles = StyleSheet.create({
  title: { fontSize: 24, fontWeight: '800', color: '#0f172a' },
  copy: { marginTop: 8, fontSize: 16, color: '#475569' },
  error: { marginTop: 8, fontSize: 16, color: '#b91c1c', fontWeight: '700' }
});
