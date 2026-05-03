import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import { useMutation } from '@tanstack/react-query';
import { StyleSheet, Text, View } from 'react-native';
import { Screen } from '../../../components/layout/Screen';
import { PrimaryButton } from '../../../components/ui/PrimaryButton';
import type { RootStackParamList } from '../../../app/navigation/types';
import { useAuthStore } from '../../auth/store/authStore';
import { createDriverProfile } from '../../../lib/deliveries';

type Props = NativeStackScreenProps<RootStackParamList, 'DriverHome'>;

export function DriverHomeScreen({ navigation }: Props) {
  const { user, activeRole, selectActiveRole, logout, bootstrap } = useAuthStore();
  const profileMutation = useMutation({
    mutationFn: () => createDriverProfile({ driverType: 'INDEPENDENT', vehicleType: 'Motorbike' }),
    onSuccess: () => bootstrap()
  });

  return (
    <Screen>
      <View style={styles.content}>
        <Text style={styles.title}>Driver dashboard</Text>
        <Text style={styles.meta}>Signed in as {user?.fullName}</Text>
        <Text style={styles.meta}>Active role: {activeRole?.role ?? 'not selected'}</Text>
        {user?.roles.map((role) => (
          <PrimaryButton key={role.id} onPress={() => selectActiveRole(role)}>Use {role.role}</PrimaryButton>
        ))}
        <PrimaryButton onPress={() => profileMutation.mutate()}>
          {profileMutation.isPending ? 'Preparing profile...' : 'Create driver profile'}
        </PrimaryButton>
        {profileMutation.isError ? <Text style={styles.error}>Could not create driver profile.</Text> : null}
        <PrimaryButton onPress={() => navigation.navigate('AvailableJobs')}>Available jobs</PrimaryButton>
        <PrimaryButton onPress={() => navigation.navigate('ActiveDelivery')}>Active delivery</PrimaryButton>
        <PrimaryButton onPress={() => navigation.navigate('CashCollection')}>Cash collection</PrimaryButton>
        <PrimaryButton onPress={() => navigation.navigate('Earnings')}>Earnings</PrimaryButton>
        <PrimaryButton onPress={() => navigation.navigate('DeliveryHistory')}>History</PrimaryButton>
        <PrimaryButton onPress={() => navigation.navigate('Support')}>Support</PrimaryButton>
        <PrimaryButton onPress={() => navigation.navigate('Notifications')}>Notifications</PrimaryButton>
        <PrimaryButton onPress={() => navigation.navigate('Profile')}>Profile</PrimaryButton>
        <PrimaryButton onPress={logout}>Logout</PrimaryButton>
      </View>
    </Screen>
  );
}

const styles = StyleSheet.create({
  content: {
    flex: 1,
    gap: 12
  },
  title: {
    fontSize: 24,
    fontWeight: '800',
    color: '#0f172a',
    marginBottom: 8
  },
  meta: {
    color: '#475569',
    fontWeight: '600'
  },
  error: { color: '#b91c1c', fontWeight: '600' }
});
