import type { PropsWithChildren } from 'react';
import { Pressable, StyleSheet, Text } from 'react-native';

type Props = PropsWithChildren<{
  onPress: () => void;
}>;

export function PrimaryButton({ children, onPress }: Props) {
  return (
    <Pressable style={styles.button} onPress={onPress}>
      <Text style={styles.label}>{children}</Text>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  button: {
    minHeight: 48,
    borderRadius: 8,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#1d4ed8',
    paddingHorizontal: 16
  },
  label: {
    color: '#ffffff',
    fontWeight: '700'
  }
});
