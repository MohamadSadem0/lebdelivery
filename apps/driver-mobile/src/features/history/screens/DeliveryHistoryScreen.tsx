import { StyleSheet, Text } from 'react-native';
import { Screen } from '../../../components/layout/Screen';

export function DeliveryHistoryScreen() {
  return (
    <Screen>
      <Text style={styles.title}>Delivery history</Text>
      <Text style={styles.copy}>Completed jobs, failed deliveries, issue reports, and payout references will appear here.</Text>
    </Screen>
  );
}

const styles = StyleSheet.create({
  title: { fontSize: 24, fontWeight: '800', color: '#0f172a' },
  copy: { marginTop: 8, fontSize: 16, color: '#475569' }
});
