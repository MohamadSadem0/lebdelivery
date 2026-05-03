import type { PropsWithChildren } from 'react';
import { StyleSheet, View } from 'react-native';

export function Screen({ children }: PropsWithChildren) {
  return <View style={styles.container}>{children}</View>;
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
    backgroundColor: '#f8fafc'
  }
});
