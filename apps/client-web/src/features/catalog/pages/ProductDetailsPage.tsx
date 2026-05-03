import { useQuery } from '@tanstack/react-query';
import { Link, useParams } from 'react-router-dom';
import { formatMoney } from '../../../lib/formatters';
import { useCartStore } from '../../cart/store/cartStore';
import { getProduct, getStore } from '../api/catalogApi';

export function ProductDetailsPage() {
  const { storeId = '', productId = '' } = useParams();
  const addProduct = useCartStore((state) => state.addProduct);
  const store = useQuery({ queryKey: ['store', storeId], queryFn: () => getStore(storeId), enabled: Boolean(storeId) });
  const product = useQuery({ queryKey: ['product', storeId, productId], queryFn: () => getProduct(storeId, productId), enabled: Boolean(storeId && productId) });

  if (!store.data || !product.data) {
    return <p className="muted">Loading product...</p>;
  }

  return (
    <section className="detail-panel">
      <Link to={`/stores/${storeId}`}>Back to store</Link>
      <h1>{product.data.name}</h1>
      <p>{product.data.description ?? product.data.stockStatus}</p>
      <strong className="price">{formatMoney(product.data.price)}</strong>
      <button className="primary-button" onClick={() => addProduct(store.data, product.data)}>Add to cart</button>
    </section>
  );
}
