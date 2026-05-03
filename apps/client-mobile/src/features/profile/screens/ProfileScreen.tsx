import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import { StyleSheet, Text, View } from 'react-native';
import { Screen } from '../../../components/layout/Screen';
import { PrimaryButton } from '../../../components/ui/PrimaryButton';
import type { RootStackParamList } from '../../../app/navigation/types';

type Props = NativeStackScreenProps<RootStackParamList, 'Profile'>;

export function ProfileScreen({ navigation }: Props) {
  return (
    <Screen>
      <View style={styles.content}>
        <Text style={styles.title}>Profile</Text>
        <Text style={styles.copy}>Manage saved addresses, receipts, support, and preferences from here.</Text>
        <PrimaryButton onPress={() => navigation.navigate('Addresses')}>Saved addresses</PrimaryButton>
      </View>
    </Screen>
  );
}

const styles = StyleSheet.create({
  content: {
    gap: 12
  },
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
