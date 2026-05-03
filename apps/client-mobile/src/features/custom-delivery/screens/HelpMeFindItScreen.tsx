import { StyleSheet, Text } from 'react-native';
import { Screen } from '../../../components/layout/Screen';

export function HelpMeFindItScreen() {
  return (
    <Screen>
      <Text style={styles.title}>Help me find it</Text>
      <Text style={styles.copy}>Intent-based search will suggest stores, service providers, delivery, or support paths later.</Text>
    </Screen>
  );
}

const styles = StyleSheet.create({
  title: { fontSize: 24, fontWeight: '800', color: '#0f172a' },
  copy: { marginTop: 8, fontSize: 16, color: '#475569' }
});
