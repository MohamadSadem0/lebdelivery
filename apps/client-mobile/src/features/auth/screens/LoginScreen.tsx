import { useState } from 'react';
import { StyleSheet, Text, TextInput, View } from 'react-native';
import { PrimaryButton } from '../../../components/ui/PrimaryButton';
import { Screen } from '../../../components/layout/Screen';
import { useAuthStore } from '../store/authStore';

export function LoginScreen() {
  const [phone, setPhone] = useState('');
  const [password, setPassword] = useState('');
  const { login, loading, error } = useAuthStore();

  return (
    <Screen>
      <View style={styles.content}>
        <Text style={styles.title}>Welcome</Text>
        <Text style={styles.copy}>Sign in with your phone number to continue.</Text>
        <TextInput
          style={styles.input}
          value={phone}
          onChangeText={setPhone}
          placeholder="+96100000000"
          keyboardType="phone-pad"
          autoCapitalize="none"
        />
        <TextInput
          style={styles.input}
          value={password}
          onChangeText={setPassword}
          placeholder="Password"
          secureTextEntry
        />
        {error ? <Text style={styles.error}>{error}</Text> : null}
        <PrimaryButton onPress={() => login({ phone, password })}>{loading ? 'Signing in...' : 'Sign in'}</PrimaryButton>
      </View>
    </Screen>
  );
}

const styles = StyleSheet.create({
  content: {
    flex: 1,
    justifyContent: 'center',
    gap: 16
  },
  title: {
    fontSize: 28,
    fontWeight: '800',
    color: '#0f172a'
  },
  copy: {
    fontSize: 16,
    lineHeight: 24,
    color: '#475569'
  },
  input: {
    minHeight: 48,
    borderRadius: 8,
    paddingHorizontal: 12,
    backgroundColor: '#ffffff',
    borderWidth: 1,
    borderColor: '#cbd5e1'
  },
  error: {
    color: '#b91c1c',
    fontWeight: '600'
  }
});
