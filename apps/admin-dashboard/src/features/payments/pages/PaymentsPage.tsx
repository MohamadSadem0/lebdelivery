import { FormEvent, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { StateBlock } from '../../../components/feedback/StateBlock';
import { formatDate, formatMoney, label } from '../../../lib/formatters';
import { createAdminCashSettlement, getAdminCashSettlements, getAdminDrivers, getAdminPayments } from '../../admin/api/adminApi';

export function PaymentsPage() {
  const queryClient = useQueryClient();
  const paymentsQuery = useQuery({ queryKey: ['admin-payments'], queryFn: getAdminPayments });
  const settlementsQuery = useQuery({ queryKey: ['admin-cash-settlements'], queryFn: getAdminCashSettlements });
  const driversQuery = useQuery({ queryKey: ['admin-drivers'], queryFn: getAdminDrivers });
  const payments = paymentsQuery.data ?? [];
  const settlements = settlementsQuery.data ?? [];
  const mismatches = payments.filter((payment) => payment.cashMismatch).length;
  const collected = payments.reduce((sum, payment) => sum + Number(payment.collectedAmount || 0), 0);
  const settled = settlements.reduce((sum, settlement) => sum + Number(settlement.amount || 0), 0);
  const unsettled = Math.max(0, collected - settled);
  const [driverId, setDriverId] = useState('');
  const [amount, setAmount] = useState('');
  const [note, setNote] = useState('');
  const settlementMutation = useMutation({
    mutationFn: createAdminCashSettlement,
    onSuccess() {
      setAmount('');
      setNote('');
      void queryClient.invalidateQueries({ queryKey: ['admin-cash-settlements'] });
      void queryClient.invalidateQueries({ queryKey: ['admin-payments'] });
    }
  });

  function submitSettlement(event: FormEvent) {
    event.preventDefault();
    if (!driverId || !amount) {
      return;
    }
    settlementMutation.mutate({ driverId, amount, note: note || undefined });
  }

  return (
    <section className="page-section">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Cash and collection</p>
          <h1>Payments</h1>
        </div>
      </div>

      <div className="metric-grid compact">
        <article className="metric-card">
          <span className="muted">Collected cash</span>
          <strong>{formatMoney(collected)}</strong>
          <p>{payments.length} payment records</p>
        </article>
        <article className="metric-card">
          <span className="muted">Settled cash</span>
          <strong>{formatMoney(settled)}</strong>
          <p>{settlements.length} settlement records</p>
        </article>
        <article className="metric-card">
          <span className="muted">Unsettled cash</span>
          <strong>{formatMoney(unsettled)}</strong>
          <p>Collected minus admin-recorded settlements</p>
        </article>
        <article className="metric-card">
          <span className="muted">Mismatches</span>
          <strong>{mismatches}</strong>
          <p>Cash records needing review</p>
        </article>
      </div>

      <form className="form-grid" onSubmit={submitSettlement}>
        <label>
          Driver
          <select value={driverId} onChange={(event) => setDriverId(event.target.value)}>
            <option value="">Select driver</option>
            {driversQuery.data?.map((driver) => (
              <option key={driver.id} value={driver.id}>
                {driver.fullName} - {driver.status}
              </option>
            ))}
          </select>
        </label>
        <label>
          Settlement amount
          <input value={amount} onChange={(event) => setAmount(event.target.value)} placeholder="0.00" inputMode="decimal" />
        </label>
        <label>
          Note
          <input value={note} onChange={(event) => setNote(event.target.value)} placeholder="Cash handed to office" />
        </label>
        <button className="primary-button" disabled={settlementMutation.isPending} type="submit">
          {settlementMutation.isPending ? 'Recording...' : 'Record settlement'}
        </button>
      </form>
      {settlementMutation.isError ? <StateBlock title="Could not record settlement" message="The amount may exceed this driver's unsettled cash." /> : null}

      {paymentsQuery.isLoading ? <StateBlock title="Loading payments" /> : null}
      {paymentsQuery.isError ? <StateBlock title="Could not load payments" /> : null}
      {!paymentsQuery.isLoading && payments.length === 0 ? <StateBlock title="No payments found" /> : null}

      {payments.length > 0 ? (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Payment</th>
                <th>Order</th>
                <th>Delivery</th>
                <th>Status</th>
                <th>Expected</th>
                <th>Collected</th>
                <th>Driver</th>
                <th>Collected at</th>
              </tr>
            </thead>
            <tbody>
              {payments.map((payment) => (
                <tr key={payment.id}>
                  <td>
                    <strong>{payment.id}</strong>
                    <span className="muted block">{label(payment.method)}</span>
                  </td>
                  <td>{payment.orderId || '-'}</td>
                  <td>{payment.deliveryId || '-'}</td>
                  <td><span className={`status-pill ${payment.cashMismatch ? 'suspended' : payment.status.toLowerCase()}`}>{label(payment.status)}</span></td>
                  <td>{formatMoney(payment.amount)}</td>
                  <td>{formatMoney(payment.collectedAmount)}</td>
                  <td>{payment.collectedByDriverId || '-'}</td>
                  <td>{formatDate(payment.collectedAt)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : null}

      {settlements.length > 0 ? (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Settlement</th>
                <th>Driver</th>
                <th>Amount</th>
                <th>Recorded by</th>
                <th>Note</th>
                <th>Created</th>
              </tr>
            </thead>
            <tbody>
              {settlements.map((settlement) => (
                <tr key={settlement.id}>
                  <td><strong>{settlement.id}</strong></td>
                  <td>{settlement.driverName || settlement.driverId}</td>
                  <td>{formatMoney(settlement.amount)}</td>
                  <td>{settlement.createdByUserName || settlement.createdByUserId || '-'}</td>
                  <td>{settlement.note || '-'}</td>
                  <td>{formatDate(settlement.createdAt)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : null}
    </section>
  );
}
