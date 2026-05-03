import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { useEffect } from 'react';
import { ActivityIndicator, View } from 'react-native';
import { CartScreen } from '../../features/cart/screens/CartScreen';
import { CheckoutScreen } from '../../features/checkout/screens/CheckoutScreen';
import { FavoritesScreen } from '../../features/favorites/screens/FavoritesScreen';
import { HomeScreen } from '../../features/home/screens/HomeScreen';
import { LoginScreen } from '../../features/auth/screens/LoginScreen';
import { OrderTrackingScreen } from '../../features/tracking/screens/OrderTrackingScreen';
import { NotificationsScreen } from '../../features/notifications/screens/NotificationsScreen';
import { OrdersScreen } from '../../features/orders/screens/OrdersScreen';
import { ProductDetailsScreen } from '../../features/products/screens/ProductDetailsScreen';
import { ProfileScreen } from '../../features/profile/screens/ProfileScreen';
import { AddressesScreen } from '../../features/profile/screens/AddressesScreen';
import { SupportScreen } from '../../features/support/screens/SupportScreen';
import { ReceiptsScreen } from '../../features/receipts/screens/ReceiptsScreen';
import { HelpMeFindItScreen } from '../../features/custom-delivery/screens/HelpMeFindItScreen';
import { RequestAnythingScreen } from '../../features/custom-delivery/screens/RequestAnythingScreen';
import { ScheduledDeliveryScreen } from '../../features/custom-delivery/screens/ScheduledDeliveryScreen';
import { UrgentNeedsScreen } from '../../features/custom-delivery/screens/UrgentNeedsScreen';
import { StoreDetailsScreen } from '../../features/stores/screens/StoreDetailsScreen';
import { StoresScreen } from '../../features/stores/screens/StoresScreen';
import { useAuthStore } from '../../features/auth/store/authStore';
import type { RootStackParamList } from './types';

const Stack = createNativeStackNavigator<RootStackParamList>();

export function RootNavigator() {
  const { user, bootstrapping, bootstrap } = useAuthStore();

  useEffect(() => {
    void bootstrap();
  }, [bootstrap]);

  if (bootstrapping) {
    return (
      <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
        <ActivityIndicator />
      </View>
    );
  }

  return (
    <Stack.Navigator>
      {!user ? (
        <Stack.Screen name="Login" component={LoginScreen} options={{ title: 'Sign in' }} />
      ) : (
        <>
      <Stack.Screen name="Home" component={HomeScreen} />
      <Stack.Screen name="Stores" component={StoresScreen} />
      <Stack.Screen name="StoreDetails" component={StoreDetailsScreen} options={{ title: 'Store details' }} />
      <Stack.Screen name="ProductDetails" component={ProductDetailsScreen} options={{ title: 'Product details' }} />
      <Stack.Screen name="Cart" component={CartScreen} />
      <Stack.Screen name="Checkout" component={CheckoutScreen} />
      <Stack.Screen name="Orders" component={OrdersScreen} />
      <Stack.Screen name="OrderTracking" component={OrderTrackingScreen} options={{ title: 'Tracking' }} />
      <Stack.Screen name="Receipts" component={ReceiptsScreen} />
      <Stack.Screen name="Notifications" component={NotificationsScreen} />
      <Stack.Screen name="Addresses" component={AddressesScreen} />
      <Stack.Screen name="UrgentNeeds" component={UrgentNeedsScreen} options={{ title: 'Urgent needs' }} />
      <Stack.Screen name="RequestAnything" component={RequestAnythingScreen} options={{ title: 'Request anything' }} />
      <Stack.Screen name="HelpMeFindIt" component={HelpMeFindItScreen} options={{ title: 'Help me find it' }} />
      <Stack.Screen name="Favorites" component={FavoritesScreen} />
      <Stack.Screen name="ScheduledDelivery" component={ScheduledDeliveryScreen} options={{ title: 'Scheduled delivery' }} />
      <Stack.Screen name="Profile" component={ProfileScreen} />
      <Stack.Screen name="Support" component={SupportScreen} />
        </>
      )}
    </Stack.Navigator>
  );
}
