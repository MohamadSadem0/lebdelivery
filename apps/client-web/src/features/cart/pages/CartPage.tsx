import { Link } from 'react-router-dom';
import { formatMoney } from '../../../lib/formatters';
import { useCartStore } from '../store/cartStore';

export function CartPage() {
  const { items, increase, decrease, removeProduct, subtotal } = useCartStore();

  return (
    <section className="stack">
      <div className="page-header">
        <h1>Cart</h1>
        <p className="muted">MVP cart supports one store per checkout.</p>
      </div>
      {items.length === 0 ? <p className="muted">Your cart is empty.</p> : null}
      <div className="stack">
        {items.map((item) => (
          <article className="card row-card" key={`${item.productId}-${item.productVariantId ?? 'base'}`}>
            <div>
              <h2>{item.name}</h2>
              <p>{formatMoney(item.unitPrice)} x {item.quantity}</p>
            </div>
            <div className="button-row">
              <button className="secondary-button" onClick={() => decrease(item.productId, item.productVariantId)}>-</button>
              <button className="secondary-button" onClick={() => increase(item.productId, item.productVariantId)}>+</button>
              <button className="danger-button" onClick={() => removeProduct(item.productId, item.productVariantId)}>Remove</button>
            </div>
          </article>
        ))}
      </div>
      <div className="summary-bar">
        <strong>Subtotal estimate: {formatMoney(subtotal())}</strong>
        <Link className="primary-button" to="/checkout">Checkout</Link>
      </div>
    </section>
  );
}
