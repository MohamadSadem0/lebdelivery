import { useQuery } from '@tanstack/react-query';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import type { Store } from '@lebanon-platform/shared-types';
import { Pressable, StyleSheet, Text, View } from 'react-native';
import { Screen } from '../../../components/layout/Screen';
import { api } from '../../../lib/api';
import type { RootStackParamList } from '../../../app/navigation/types';

type Props = NativeStackScreenProps<RootStackParamList, 'Stores'>;

type ApiEnvelope<T> = {
  data: T;
};

export function StoresScreen({ navigation }: Props) {
  const stores = useQuery({
    queryKey: ['stores'],
    queryFn: async () => {
      const response = await api.get<ApiEnvelope<Store[]>>('/stores?page=0&size=20');
      return response.data.data;
    }
  });

  return (
    <Screen>
      <Text style={styles.title}>Stores</Text>
      {stores.isLoading ? <Text style={styles.copy}>Loading stores...</Text> : null}
      {stores.isError ? <Text style={styles.copy}>Stores are unavailable right now.</Text> : null}
      <View style={styles.list}>
        {stores.data?.map((store) => (
          <Pressable key={store.id} style={styles.item} onPress={() => navigation.navigate('StoreDetails', { storeId: store.id })}>
            <Text style={styles.name}>{store.name}</Text>
            <Text style={styles.copy}>{store.storeTypeCode} - {store.deliveryMode}</Text>
            <Text style={[styles.badge, store.openNow ? styles.badgeOpen : styles.badgeClosed]}>
              {store.openNow ? 'Open now' : 'Closed'}
            </Text>
          </Pressable>
        ))}
      </View>
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
  },
  list: {
    marginTop: 16,
    gap: 10
  },
  item: {
    borderRadius: 8,
    backgroundColor: '#ffffff',
    padding: 14,
    borderWidth: 1,
    borderColor: '#e2e8f0'
  },
  name: {
    fontSize: 16,
    fontWeight: '800',
    color: '#0f172a'
  },
  badge: {
    alignSelf: 'flex-start',
    marginTop: 8,
    borderRadius: 999,
    overflow: 'hidden',
    paddingHorizontal: 10,
    paddingVertical: 4,
    fontWeight: '800'
  },
  badgeOpen: { backgroundColor: '#ccfbf1', color: '#115e59' },
  badgeClosed: { backgroundColor: '#e2e8f0', color: '#475569' }
});
