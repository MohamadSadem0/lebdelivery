import { StyleSheet, Text } from 'react-native';
import { Screen } from '../../../components/layout/Screen';

export function FavoritesScreen() {
  return (
    <Screen>
      <Text style={styles.title}>Favorites</Text>
      <Text style={styles.copy}>Favorite stores, products, and reorder shortcuts will be added later.</Text>
    </Screen>
  );
}

const styles = StyleSheet.create({
  title: { fontSize: 24, fontWeight: '800', color: '#0f172a' },
  copy: { marginTop: 8, fontSize: 16, color: '#475569' }
});
