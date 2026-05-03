import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import { useQuery } from '@tanstack/react-query';
import type { Product } from '@lebanon-platform/shared-types';
import { StyleSheet, Text } from 'react-native';
import { Screen } from '../../../components/layout/Screen';
import type { RootStackParamList } from '../../../app/navigation/types';
import { api } from '../../../lib/api';
import { PrimaryButton } from '../../../components/ui/PrimaryButton';
import { useCartStore } from '../../cart/store/cartStore';

type Props = NativeStackScreenProps<RootStackParamList, 'ProductDetails'>;
type ApiEnvelope<T> = { data: T };

export function ProductDetailsScreen({ route }: Props) {
  const addItem = useCartStore((state) => state.addItem);
  const product = useQuery({
    queryKey: ['product', route.params.storeId, route.params.productId],
    queryFn: async () => {
      const response = await api.get<ApiEnvelope<Product>>(`/stores/${route.params.storeId}/products/${route.params.productId}`);
      return response.data.data;
    }
  });

  return (
    <Screen>
      <Text style={styles.title}>{product.data?.name ?? 'Product details'}</Text>
      {product.isLoading ? <Text style={styles.copy}>Loading product...</Text> : null}
      {product.isError ? <Text style={styles.copy}>Product details are unavailable.</Text> : null}
      {product.data ? (
        <>
          <Text style={styles.copy}>{product.data.description ?? 'No description yet.'}</Text>
          <Text style={styles.copy}>Price: {product.data.price}</Text>
          <Text style={styles.copy}>Stock: {product.data.stockStatus}</Text>
          <PrimaryButton
            onPress={() =>
              addItem({
                productId: product.data.id,
                storeId: route.params.storeId,
                name: product.data.name,
                imageUrl: product.data.imageUrl,
                unitPrice: product.data.price,
                quantity: 1
              })
            }
          >
            Add to cart
          </PrimaryButton>
        </>
      ) : null}
    </Screen>
  );
}

const styles = StyleSheet.create({
  title: { fontSize: 24, fontWeight: '800', color: '#0f172a' },
  copy: { marginTop: 8, fontSize: 16, color: '#475569' }
});
