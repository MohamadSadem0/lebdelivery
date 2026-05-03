import { StyleSheet, Text } from 'react-native';
import { Screen } from '../../../components/layout/Screen';

export function EarningsScreen() {
  return (
    <Screen>
      <Text style={styles.title}>Earnings</Text>
      <Text style={styles.copy}>Earnings summaries and cash reconciliation are intentionally deferred.</Text>
    </Screen>
  );
}

const styles = StyleSheet.create({
  title: {
    fontSize: 24,
    fontWeight: '800',
    color: '#0f172a'
  },
  copy: {
    marginTop: 8,
    fontSize: 16,
    color: '#475569'
  }
});
