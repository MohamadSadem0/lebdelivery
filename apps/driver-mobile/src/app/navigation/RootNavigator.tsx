import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { useEffect } from 'react';
import { ActivityIndicator, View } from 'react-native';
import { LoginScreen } from '../../features/auth/screens/LoginScreen';
import { ActiveDeliveryScreen } from '../../features/active-delivery/screens/ActiveDeliveryScreen';
import { CashCollectionScreen } from '../../features/cash-collection/screens/CashCollectionScreen';
import { DeliveryHistoryScreen } from '../../features/history/screens/DeliveryHistoryScreen';
import { AvailableJobsScreen } from '../../features/jobs/screens/AvailableJobsScreen';
import { JobDetailsScreen } from '../../features/jobs/screens/JobDetailsScreen';
import { DriverHomeScreen } from '../../features/availability/screens/DriverHomeScreen';
import { EarningsScreen } from '../../features/earnings/screens/EarningsScreen';
import { ProfileScreen } from '../../features/profile/screens/ProfileScreen';
import { SupportScreen } from '../../features/support/screens/SupportScreen';
import { NotificationsScreen } from '../../features/notifications/screens/NotificationsScreen';
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
        <Stack.Screen name="Login" component={LoginScreen} options={{ title: 'Driver sign in' }} />
      ) : (
        <>
      <Stack.Screen name="DriverHome" component={DriverHomeScreen} options={{ title: 'Driver home' }} />
      <Stack.Screen name="AvailableJobs" component={AvailableJobsScreen} options={{ title: 'Available jobs' }} />
      <Stack.Screen name="JobDetails" component={JobDetailsScreen} options={{ title: 'Job details' }} />
      <Stack.Screen name="ActiveDelivery" component={ActiveDeliveryScreen} options={{ title: 'Active delivery' }} />
      <Stack.Screen name="CashCollection" component={CashCollectionScreen} options={{ title: 'Cash collection' }} />
      <Stack.Screen name="Earnings" component={EarningsScreen} />
      <Stack.Screen name="DeliveryHistory" component={DeliveryHistoryScreen} options={{ title: 'Delivery history' }} />
      <Stack.Screen name="Profile" component={ProfileScreen} />
      <Stack.Screen name="Support" component={SupportScreen} />
      <Stack.Screen name="Notifications" component={NotificationsScreen} />
        </>
      )}
    </Stack.Navigator>
  );
}
