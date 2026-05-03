import { useMutation, useQuery } from '@tanstack/react-query';
import { FormEvent, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { formatMoney } from '../../../lib/formatters';
import { useCartStore } from '../../cart/store/cartStore';
import { createOrder, getAddresses } from '../api/clientApi';

export function CheckoutPage() {
  const navigate = useNavigate();
  const { items, storeId, clear, subtotal } = useCartStore();
  const addresses = useQuery({ queryKey: ['client-addresses'], queryFn: getAddresses });
  const [selectedAddressId, setSelectedAddressId] = useState<string | undefined>();
  const [manualAddress, setManualAddress] = useState('Hamra, Beirut');
  const [notes, setNotes] = useState('');
  const [needsChange, setNeedsChange] = useState(false);
  const [cashAmountClientHas, setCashAmountClientHas] = useState('');
  const [fulfillmentType, setFulfillmentType] = useState<'NOW' | 'SCHEDULED'>('NOW');
  const [scheduledFor, setScheduledFor] = useState('');

  useEffect(() => {
    if (!selectedAddressId && addresses.data && addresses.data.length > 0) {
      setSelectedAddressId(addresses.data.find((address) => address.defaultAddress)?.id ?? addresses.data[0].id);
    }
  }, [addresses.data, selectedAddressId]);

  const mutation = useMutation({
    mutationFn: createOrder,
    onSuccess(order) {
      clear();
      navigate(`/orders/${order.id}`);
    }
  });

  function submit(event: FormEvent) {
    event.preventDefault();
    if (!storeId || items.length === 0) {
      return;
    }
    mutation.mutate({
      storeId,
      paymentMethod: 'CASH_ON_DELIVERY',
      fulfillmentType,
      scheduledFor: fulfillmentType === 'SCHEDULED' && scheduledFor ? new Date(scheduledFor).toISOString() : undefined,
      needsChange,
      cashAmountClientHas: cashAmountClientHas || undefined,
      notes,
      addressId: selectedAddressId,
      address: selectedAddressId ? undefined : { label: 'Manual', fullAddress: manualAddress },
      items: items.map((item) => ({
        productId: item.productId,
        productVariantId: item.productVariantId,
        quantity: item.quantity
      }))
    });
  }

  return (
    <form className="stack" onSubmit={submit}>
      <div className="page-header">
        <h1>Checkout</h1>
        <p className="muted">Cash on delivery is selected for the MVP.</p>
      </div>
      {items.map((item) => (
        <article className="card row-card" key={`${item.productId}-${item.productVariantId ?? 'base'}`}>
          <h2>{item.name}</h2>
          <p>{formatMoney(item.unitPrice)} x {item.quantity}</p>
        </article>
      ))}
      <strong>Subtotal estimate: {formatMoney(subtotal())}</strong>

      <section className="form-card">
        <h2>Fulfillment</h2>
        <label className="radio-card">
          <input type="radio" checked={fulfillmentType === 'NOW'} onChange={() => setFulfillmentType('NOW')} />
          <span>
            <strong>Order now</strong>
            <small>The store reviews this order as soon as possible.</small>
          </span>
        </label>
        <label className="radio-card">
          <input type="radio" checked={fulfillmentType === 'SCHEDULED'} onChange={() => setFulfillmentType('SCHEDULED')} />
          <span>
            <strong>Schedule for later</strong>
            <small>Use a time at least 30 minutes from now and within 14 days.</small>
          </span>
        </label>
        {fulfillmentType === 'SCHEDULED' ? (
          <label>
            Scheduled time
            <input type="datetime-local" value={scheduledFor} onChange={(event) => setScheduledFor(event.target.value)} />
          </label>
        ) : null}
      </section>

      <section className="form-card">
        <h2>Delivery address</h2>
        {addresses.data?.map((address) => (
          <label className="radio-card" key={address.id}>
            <input type="radio" checked={selectedAddressId === address.id} onChange={() => setSelectedAddressId(address.id)} />
            <span>
              <strong>{address.label}{address.defaultAddress ? ' - Default' : ''}</strong>
              <small>{address.fullAddress}</small>
            </span>
          </label>
        ))}
        {addresses.data && addresses.data.length > 0 ? (
          <button className="link-button" type="button" onClick={() => setSelectedAddressId(undefined)}>
            Use a different address for this order
          </button>
        ) : null}
        {!selectedAddressId ? (
          <label>
            Manual address
            <input value={manualAddress} onChange={(event) => setManualAddress(event.target.value)} />
          </label>
        ) : null}
      </section>

      <section className="form-card">
        <label>
          Notes
          <input value={notes} onChange={(event) => setNotes(event.target.value)} placeholder="Call before arriving" />
        </label>
        <label className="checkbox-row">
          <input type="checkbox" checked={needsChange} onChange={(event) => setNeedsChange(event.target.checked)} />
          Need change for cash
        </label>
        <label>
          Cash amount client has
          <input value={cashAmountClientHas} onChange={(event) => setCashAmountClientHas(event.target.value)} inputMode="decimal" />
        </label>
      </section>

      {mutation.isError ? <p className="error-text">Could not place order. Check stock and try again.</p> : null}
      <button className="primary-button" disabled={mutation.isPending || items.length === 0} type="submit">
        {mutation.isPending ? 'Placing order...' : 'Place order'}
      </button>
    </form>
  );
}
