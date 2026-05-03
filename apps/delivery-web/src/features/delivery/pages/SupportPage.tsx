import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { FormEvent, useState } from 'react';
import { createSupportTicket, getSupportTickets } from '../api/deliveryApi';

export function SupportPage() {
  const queryClient = useQueryClient();
  const tickets = useQuery({ queryKey: ['support-tickets'], queryFn: getSupportTickets });
  const [subject, setSubject] = useState('');
  const [description, setDescription] = useState('');
  const mutation = useMutation({
    mutationFn: createSupportTicket,
    onSuccess() {
      setSubject('');
      setDescription('');
      void queryClient.invalidateQueries({ queryKey: ['support-tickets'] });
    }
  });

  function submit(event: FormEvent) {
    event.preventDefault();
    if (!subject.trim() || !description.trim()) {
      return;
    }
    mutation.mutate({ subject, description });
  }

  return (
    <section className="stack">
      <div className="page-header">
        <h1>Support</h1>
        <p className="muted">Create delivery or cash collection issue tickets.</p>
      </div>
      <form className="form-card" onSubmit={submit}>
        <label>
          Subject
          <input value={subject} onChange={(event) => setSubject(event.target.value)} />
        </label>
        <label>
          Description
          <textarea value={description} onChange={(event) => setDescription(event.target.value)} />
        </label>
        <button className="primary-button" disabled={mutation.isPending} type="submit">
          {mutation.isPending ? 'Creating...' : 'Create ticket'}
        </button>
      </form>
      <div className="stack">
        {tickets.data?.map((ticket) => (
          <article className="card" key={ticket.id}>
            <h2>{ticket.subject}</h2>
            <p>{ticket.description}</p>
            <span className="badge">{ticket.status}</span>
          </article>
        ))}
      </div>
    </section>
  );
}
