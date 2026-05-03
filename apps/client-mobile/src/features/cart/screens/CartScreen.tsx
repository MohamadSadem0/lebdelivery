import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import { StyleSheet, Text, View } from 'react-native';
import { Screen } from '../../../components/layout/Screen';
import { PrimaryButton } from '../../../components/ui/PrimaryButton';
import type { RootStackParamList } from '../../../app/navigation/types';
import { useCartStore } from '../store/cartStore';

type Props = NativeStackScreenProps<RootStackParamList, 'Cart'>;

export function CartScreen({ navigation }: Props) {
  const { items, increase, decrease, removeItem, subtotal } = useCartStore();

  return (
    <Screen>
      <Text style={styles.title}>Cart</Text>
      {items.length === 0 ? <Text style={styles.copy}>Your cart is empty.</Text> : null}
      <View style={styles.list}>
        {items.map((item) => (
          <View key={`${item.productId}-${item.productVariantId ?? 'base'}`} style={styles.item}>
            <Text style={styles.name}>{item.name}</Text>
            <Text style={styles.copy}>{item.unitPrice} x {item.quantity}</Text>
            <View style={styles.row}>
              <PrimaryButton onPress={() => decrease(item.productId, item.productVariantId)}>-</PrimaryButton>
              <PrimaryButton onPress={() => increase(item.productId, item.productVariantId)}>+</PrimaryButton>
              <PrimaryButton onPress={() => removeItem(item.productId, item.productVariantId)}>Remove</PrimaryButton>
            </View>
          </View>
        ))}
      </View>
      <Text style={styles.total}>Subtotal estimate: {subtotal().toFixed(2)}</Text>
      {items.length > 0 ? <PrimaryButton onPress={() => navigation.navigate('Checkout')}>Checkout</PrimaryButton> : null}
    </Screen>
  );
}

const styles = StyleSheet.create({
  title: { fontSize: 24, fontWeight: '800', color: '#0f172a' },
  copy: { marginTop: 8, fontSize: 16, color: '#475569' },
  list: { marginTop: 16, gap: 10 },
  item: { borderRadius: 8, backgroundColor: '#ffffff', padding: 14, borderWidth: 1, borderColor: '#e2e8f0', gap: 8 },
  name: { fontSize: 16, fontWeight: '800', color: '#0f172a' },
  row: { flexDirection: 'row', gap: 8, flexWrap: 'wrap' },
  total: { marginVertical: 16, fontSize: 18, fontWeight: '800', color: '#0f172a' }
});
