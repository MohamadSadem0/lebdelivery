import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import { useQuery } from '@tanstack/react-query';
import type { Product, Store } from '@lebanon-platform/shared-types';
import { Pressable, StyleSheet, Text, View } from 'react-native';
import { Screen } from '../../../components/layout/Screen';
import type { RootStackParamList } from '../../../app/navigation/types';
import { api } from '../../../lib/api';
import { PrimaryButton } from '../../../components/ui/PrimaryButton';
import { useCartStore } from '../../cart/store/cartStore';

type Props = NativeStackScreenProps<RootStackParamList, 'StoreDetails'>;
type ApiEnvelope<T> = { data: T };

export function StoreDetailsScreen({ route, navigation }: Props) {
  const addItem = useCartStore((state) => state.addItem);
  const store = useQuery({
    queryKey: ['store', route.params.storeId],
    queryFn: async () => {
      const response = await api.get<ApiEnvelope<Store>>(`/stores/${route.params.storeId}`);
      return response.data.data;
    }
  });

  const products = useQuery({
    queryKey: ['store-products', route.params.storeId],
    queryFn: async () => {
      const response = await api.get<ApiEnvelope<Product[]>>(`/stores/${route.params.storeId}/products?page=0&size=20`);
      return response.data.data;
    }
  });

  return (
    <Screen>
      <Text style={styles.title}>{store.data?.name ?? 'Store details'}</Text>
      {store.isLoading ? <Text style={styles.copy}>Loading store...</Text> : null}
      {store.isError ? <Text style={styles.copy}>Store details are unavailable.</Text> : null}
      {store.data ? (
        <>
          <Text style={styles.copy}>{store.data.description ?? store.data.address ?? store.data.storeTypeCode}</Text>
          <Text style={[styles.badge, store.data.openNow ? styles.badgeOpen : styles.badgeClosed]}>
            {store.data.openNow ? 'Open now' : 'Closed now - schedule for later'}
          </Text>
        </>
      ) : null}
      <Text style={styles.sectionTitle}>Products</Text>
      <View style={styles.list}>
        {products.data?.map((product) => (
          <Pressable
            key={product.id}
            style={styles.item}
            onPress={() => navigation.navigate('ProductDetails', { storeId: route.params.storeId, productId: product.id })}
          >
            <Text style={styles.name}>{product.name}</Text>
            <Text style={styles.copy}>{product.price} - {product.stockStatus}</Text>
            <PrimaryButton
              onPress={() =>
                addItem({
                  productId: product.id,
                  storeId: route.params.storeId,
                  storeName: store.data?.name,
                  name: product.name,
                  imageUrl: product.imageUrl,
                  unitPrice: product.price,
                  quantity: 1
                })
              }
            >
              Add to cart
            </PrimaryButton>
          </Pressable>
        ))}
      </View>
    </Screen>
  );
}

const styles = StyleSheet.create({
  title: { fontSize: 24, fontWeight: '800', color: '#0f172a' },
  sectionTitle: { marginTop: 22, fontSize: 18, fontWeight: '800', color: '#0f172a' },
  copy: { marginTop: 8, fontSize: 16, color: '#475569' },
  list: { marginTop: 12, gap: 10 },
  item: {
    borderRadius: 8,
    backgroundColor: '#ffffff',
    padding: 14,
    borderWidth: 1,
    borderColor: '#e2e8f0'
  },
  name: { fontSize: 16, fontWeight: '800', color: '#0f172a' },
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
