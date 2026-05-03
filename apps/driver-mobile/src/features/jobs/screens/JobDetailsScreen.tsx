import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import { useMutation, useQuery } from '@tanstack/react-query';
import { StyleSheet, Text, View } from 'react-native';
import { Screen } from '../../../components/layout/Screen';
import { PrimaryButton } from '../../../components/ui/PrimaryButton';
import type { RootStackParamList } from '../../../app/navigation/types';
import { acceptJob, getDriverJob } from '../../../lib/deliveries';

type Props = NativeStackScreenProps<RootStackParamList, 'JobDetails'>;

export function JobDetailsScreen({ route, navigation }: Props) {
  const jobQuery = useQuery({ queryKey: ['driver-job', route.params.jobId], queryFn: () => getDriverJob(route.params.jobId) });
  const acceptMutation = useMutation({
    mutationFn: () => acceptJob(route.params.jobId),
    onSuccess: (delivery) => navigation.replace('ActiveDelivery', { deliveryId: delivery.id })
  });
  const job = jobQuery.data;

  return (
    <Screen>
      <View style={styles.content}>
        <Text style={styles.title}>Job {route.params.jobId}</Text>
        {jobQuery.isLoading ? <Text style={styles.item}>Loading job...</Text> : null}
        {jobQuery.isError ? <Text style={styles.error}>Job is not available.</Text> : null}
        {job ? (
          <>
            <Text style={styles.item}>Estimated fee: {Number(job.deliveryFee).toFixed(2)}</Text>
            <Text style={styles.item}>Pickup: {job.pickupAddressSnapshot ?? 'Pending'}</Text>
            <Text style={styles.item}>Dropoff: {job.dropoffAddressSnapshot ?? 'Pending'}</Text>
            <Text style={styles.item}>Status: {job.status}</Text>
            <Text style={styles.item}>Cash collection required: likely COD</Text>
          </>
        ) : null}
        {acceptMutation.isError ? <Text style={styles.error}>Could not accept this job.</Text> : null}
        <PrimaryButton onPress={() => acceptMutation.mutate()}>{acceptMutation.isPending ? 'Accepting...' : 'Accept job'}</PrimaryButton>
        <PrimaryButton onPress={() => navigation.goBack()}>Reject job</PrimaryButton>
      </View>
    </Screen>
  );
}

const styles = StyleSheet.create({
  content: { flex: 1, gap: 10 },
  title: { fontSize: 24, fontWeight: '800', color: '#0f172a', marginBottom: 8 },
  item: {
    minHeight: 36,
    borderRadius: 8,
    paddingHorizontal: 12,
    paddingVertical: 8,
    backgroundColor: '#e2e8f0',
    color: '#0f172a',
    fontWeight: '600'
  },
  error: { color: '#b91c1c', fontWeight: '700' }
});
