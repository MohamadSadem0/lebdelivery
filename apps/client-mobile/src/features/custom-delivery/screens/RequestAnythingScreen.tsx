import { StyleSheet, Text } from 'react-native';
import { Screen } from '../../../components/layout/Screen';

export function RequestAnythingScreen() {
  return (
    <Screen>
      <Text style={styles.title}>Request anything</Text>
      <Text style={styles.copy}>Concierge-style requests will be routed to stores, providers, support, or delivery workflows later.</Text>
    </Screen>
  );
}

const styles = StyleSheet.create({
  title: { fontSize: 24, fontWeight: '800', color: '#0f172a' },
  copy: { marginTop: 8, fontSize: 16, color: '#475569' }
});
