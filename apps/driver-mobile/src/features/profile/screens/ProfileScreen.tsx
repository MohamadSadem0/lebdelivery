import { StyleSheet, Text } from 'react-native';
import { Screen } from '../../../components/layout/Screen';

export function ProfileScreen() {
  return (
    <Screen>
      <Text style={styles.title}>Profile</Text>
      <Text style={styles.copy}>Driver profile, documents, approval state, and support entry points will be wired later.</Text>
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
