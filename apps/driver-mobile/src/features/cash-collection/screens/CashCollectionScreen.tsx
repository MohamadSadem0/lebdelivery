import { StyleSheet, Text } from 'react-native';
import { Screen } from '../../../components/layout/Screen';

export function CashCollectionScreen() {
  return (
    <Screen>
      <Text style={styles.title}>Cash collection</Text>
      <Text style={styles.copy}>Cash-on-delivery collection, handoff, and reconciliation placeholders live here.</Text>
    </Screen>
  );
}

const styles = StyleSheet.create({
  title: { fontSize: 24, fontWeight: '800', color: '#0f172a' },
  copy: { marginTop: 8, fontSize: 16, color: '#475569' }
});
