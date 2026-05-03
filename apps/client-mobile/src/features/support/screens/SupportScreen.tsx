import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { StyleSheet, Text, TextInput, View } from 'react-native';
import { Screen } from '../../../components/layout/Screen';
import { PrimaryButton } from '../../../components/ui/PrimaryButton';
import { createSupportTicket, getSupportTickets } from '../../../lib/support';

export function SupportScreen() {
  const queryClient = useQueryClient();
  const [subject, setSubject] = useState('');
  const [description, setDescription] = useState('');
  const ticketsQuery = useQuery({ queryKey: ['support-tickets'], queryFn: getSupportTickets });
  const createMutation = useMutation({
    mutationFn: createSupportTicket,
    onSuccess: async () => {
      setSubject('');
      setDescription('');
      await queryClient.invalidateQueries({ queryKey: ['support-tickets'] });
    }
  });

  return (
    <Screen>
      <Text style={styles.title}>Support</Text>
      <View style={styles.form}>
        <TextInput style={styles.input} value={subject} onChangeText={setSubject} placeholder="Subject" />
        <TextInput
          style={[styles.input, styles.textarea]}
          value={description}
          onChangeText={setDescription}
          placeholder="Describe the issue"
          multiline
        />
        <PrimaryButton onPress={() => createMutation.mutate({ subject, description })}>
          {createMutation.isPending ? 'Sending...' : 'Create ticket'}
        </PrimaryButton>
        {createMutation.isError ? <Text style={styles.error}>Could not create ticket.</Text> : null}
      </View>

      {ticketsQuery.isLoading ? <Text style={styles.copy}>Loading tickets...</Text> : null}
      {ticketsQuery.isError ? <Text style={styles.error}>Tickets could not be loaded.</Text> : null}
      {ticketsQuery.data?.length === 0 ? <Text style={styles.copy}>No tickets yet.</Text> : null}
      {ticketsQuery.data?.map((ticket) => (
        <View key={ticket.id} style={styles.ticket}>
          <Text style={styles.ticketTitle}>{ticket.subject}</Text>
          <Text style={styles.copy}>{ticket.description}</Text>
          <Text style={styles.meta}>{ticket.status.replaceAll('_', ' ')}</Text>
          {ticket.adminNote ? <Text style={styles.copy}>Support: {ticket.adminNote}</Text> : null}
        </View>
      ))}
    </Screen>
  );
}

const styles = StyleSheet.create({
  title: { fontSize: 24, fontWeight: '800', color: '#0f172a' },
  form: { gap: 10, marginTop: 12, marginBottom: 14 },
  input: { minHeight: 44, borderRadius: 8, borderWidth: 1, borderColor: '#cbd5e1', paddingHorizontal: 12, backgroundColor: '#fff' },
  textarea: { minHeight: 90, paddingTop: 10, textAlignVertical: 'top' },
  ticket: { borderRadius: 8, backgroundColor: '#f1f5f9', padding: 12, marginTop: 10, gap: 6 },
  ticketTitle: { fontSize: 16, fontWeight: '800', color: '#0f172a' },
  copy: { fontSize: 15, color: '#475569' },
  meta: { color: '#0f766e', fontWeight: '800' },
  error: { marginTop: 8, fontSize: 15, color: '#b91c1c', fontWeight: '700' }
});
