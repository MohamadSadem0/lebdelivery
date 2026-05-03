import { useQuery } from '@tanstack/react-query';
import { Link, useParams } from 'react-router-dom';
import { formatMoney } from '../../../lib/formatters';
import { useCartStore } from '../../cart/store/cartStore';
import { getStore, getStoreProducts } from '../api/catalogApi';

export function StoreDetailsPage() {
  const { storeId = '' } = useParams();
  const addProduct = useCartStore((state) => state.addProduct);
  const store = useQuery({ queryKey: ['store', storeId], queryFn: () => getStore(storeId), enabled: Boolean(storeId) });
  const products = useQuery({ queryKey: ['store-products', storeId], queryFn: () => getStoreProducts(storeId), enabled: Boolean(storeId) });

  if (store.isLoading) {
    return <p className="muted">Loading store...</p>;
  }
  if (!store.data) {
    return <p className="error-text">Store is unavailable.</p>;
  }

  return (
    <section className="stack">
      <div className="page-header">
        <p className="eyebrow">{store.data.storeTypeCode}</p>
        <h1>{store.data.name}</h1>
        <p className="muted">{store.data.description ?? store.data.address}</p>
        <span className={`badge ${store.data.openNow ? '' : 'badge-muted'}`}>{store.data.openNow ? 'Open now' : 'Closed now - schedule for later'}</span>
      </div>
      {products.isLoading ? <p className="muted">Loading products...</p> : null}
      <div className="card-grid">
        {products.data?.map((product) => (
          <article className="card" key={product.id}>
            <h2>{product.name}</h2>
            <p>{product.description ?? product.stockStatus}</p>
            <strong>{formatMoney(product.price)}</strong>
            <div className="button-row">
              <Link className="secondary-button" to={`/stores/${storeId}/products/${product.id}`}>Details</Link>
              <button className="primary-button" onClick={() => addProduct(store.data, product)}>Add to cart</button>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}
