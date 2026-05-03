import { StyleSheet, Text } from 'react-native';
import { Screen } from '../../../components/layout/Screen';

export function UrgentNeedsScreen() {
  return (
    <Screen>
      <Text style={styles.title}>Urgent needs</Text>
      <Text style={styles.copy}>Pharmacy, baby items, water, gas, grocery, documents, repair pickup, and laundry shortcuts will be organized here.</Text>
    </Screen>
  );
}

const styles = StyleSheet.create({
  title: { fontSize: 24, fontWeight: '800', color: '#0f172a' },
  copy: { marginTop: 8, fontSize: 16, color: '#475569' }
});
