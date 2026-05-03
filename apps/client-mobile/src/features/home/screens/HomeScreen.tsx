import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import { StyleSheet, Text, View } from 'react-native';
import { PrimaryButton } from '../../../components/ui/PrimaryButton';
import { Screen } from '../../../components/layout/Screen';
import type { RootStackParamList } from '../../../app/navigation/types';
import { useAuthStore } from '../../auth/store/authStore';

type Props = NativeStackScreenProps<RootStackParamList, 'Home'>;

export function HomeScreen({ navigation }: Props) {
  const { user, activeRole, selectActiveRole, logout } = useAuthStore();
  const sections = [
    'Nearby stores',
    'Fastest delivery',
    'Open now',
    'Pharmacy now',
    'Quick essentials',
    'Send package',
    'Repair and laundry',
    'Urgent needs',
    'Reorder again'
  ];

  return (
    <Screen>
      <View style={styles.content}>
        <Text style={styles.title}>Local commerce</Text>
        <Text style={styles.meta}>Signed in as {user?.fullName}</Text>
        <Text style={styles.meta}>Active role: {activeRole?.role ?? 'not selected'}</Text>
        {user?.roles.map((role) => (
          <PrimaryButton key={role.id} onPress={() => selectActiveRole(role)}>Use {role.role}</PrimaryButton>
        ))}
        <View style={styles.sectionGrid}>
          {sections.map((section) => (
            <Text key={section} style={styles.sectionItem}>{section}</Text>
          ))}
        </View>
        <PrimaryButton onPress={() => navigation.navigate('Stores')}>Browse stores</PrimaryButton>
        <PrimaryButton onPress={() => navigation.navigate('UrgentNeeds')}>Urgent needs</PrimaryButton>
        <PrimaryButton onPress={() => navigation.navigate('RequestAnything')}>Request anything</PrimaryButton>
        <PrimaryButton onPress={() => navigation.navigate('HelpMeFindIt')}>Help me find it</PrimaryButton>
        <PrimaryButton onPress={() => navigation.navigate('Orders')}>View orders</PrimaryButton>
        <PrimaryButton onPress={() => navigation.navigate('Notifications')}>Notifications</PrimaryButton>
        <PrimaryButton onPress={() => navigation.navigate('Support')}>Support</PrimaryButton>
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
  sectionGrid: {
    gap: 8,
    marginBottom: 12
  },
  sectionItem: {
    minHeight: 36,
    borderRadius: 8,
    paddingHorizontal: 12,
    paddingVertical: 8,
    backgroundColor: '#e2e8f0',
    color: '#0f172a',
    fontWeight: '600'
  }
});
