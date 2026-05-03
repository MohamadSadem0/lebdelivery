import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import { useQuery } from '@tanstack/react-query';
import { Pressable, StyleSheet, Text, View } from 'react-native';
import { Screen } from '../../../components/layout/Screen';
import type { RootStackParamList } from '../../../app/navigation/types';
import { getAvailableJobs } from '../../../lib/deliveries';

type Props = NativeStackScreenProps<RootStackParamList, 'AvailableJobs'>;

export function AvailableJobsScreen({ navigation }: Props) {
  const jobsQuery = useQuery({ queryKey: ['available-jobs'], queryFn: getAvailableJobs });

  return (
    <Screen>
      <View style={styles.content}>
        <Text style={styles.title}>Available jobs</Text>
        {jobsQuery.isLoading ? <Text style={styles.copy}>Loading jobs...</Text> : null}
        {jobsQuery.isError ? <Text style={styles.error}>Create a driver profile first, then try again.</Text> : null}
        {jobsQuery.data?.length === 0 ? <Text style={styles.copy}>No waiting deliveries right now.</Text> : null}
        {jobsQuery.data?.map((job) => (
          <Pressable key={job.id} style={styles.job} onPress={() => navigation.navigate('JobDetails', { jobId: job.id })}>
            <Text style={styles.jobTitle}>{job.storeName ?? 'Store delivery'}</Text>
            <Text style={styles.copy}>{job.pickupAddressSnapshot ?? 'Pickup address pending'}</Text>
            <Text style={styles.copy}>{job.dropoffAddressSnapshot ?? 'Dropoff address pending'}</Text>
            <Text style={styles.badge}>{job.status}</Text>
          </Pressable>
        ))}
      </View>
    </Screen>
  );
}

const styles = StyleSheet.create({
  content: { flex: 1, gap: 12 },
  title: {
    fontSize: 24,
    fontWeight: '800',
    color: '#0f172a'
  },
  copy: {
    fontSize: 16,
    color: '#475569'
  },
  error: { fontSize: 15, color: '#b91c1c', fontWeight: '700' },
  job: { gap: 6, borderRadius: 8, padding: 12, backgroundColor: '#ffffff', borderWidth: 1, borderColor: '#cbd5e1' },
  jobTitle: { fontSize: 17, fontWeight: '800', color: '#0f172a' },
  badge: { alignSelf: 'flex-start', borderRadius: 6, overflow: 'hidden', paddingHorizontal: 8, paddingVertical: 4, backgroundColor: '#dbeafe', color: '#1d4ed8', fontWeight: '800' }
});
