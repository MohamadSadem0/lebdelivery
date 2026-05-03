import { StyleSheet, Text } from 'react-native';
import { Screen } from '../../../components/layout/Screen';

export function ScheduledDeliveryScreen() {
  return (
    <Screen>
      <Text style={styles.title}>Scheduled delivery</Text>
      <Text style={styles.copy}>Future fulfillment windows and scheduled custom delivery requests will live here.</Text>
    </Screen>
  );
}

const styles = StyleSheet.create({
  title: { fontSize: 24, fontWeight: '800', color: '#0f172a' },
  copy: { marginTop: 8, fontSize: 16, color: '#475569' }
});
