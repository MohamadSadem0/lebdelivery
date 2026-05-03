import { useQuery } from '@tanstack/react-query';
import { StateBlock } from '../../../components/feedback/StateBlock';
import { formatMoney } from '../../../lib/formatters';
import { useStoreSelectionStore } from '../../stores/store/storeSelectionStore';
import { getStoreProducts } from '../api/productsApi';

export function ProductsPage() {
  const selectedStoreId = useStoreSelectionStore((state) => state.selectedStoreId);
  const productsQuery = useQuery({
    queryKey: ['store-products', selectedStoreId],
    queryFn: () => getStoreProducts(selectedStoreId as string),
    enabled: Boolean(selectedStoreId)
  });

  if (!selectedStoreId) {
    return <StateBlock title="Choose a store" message="Select a store from the top bar to view products." />;
  }

  const products = productsQuery.data ?? [];

  return (
    <section className="page-section">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Catalog</p>
          <h1>Products</h1>
        </div>
        <button className="primary-button" disabled type="button">Create product soon</button>
      </div>

      {productsQuery.isLoading ? <StateBlock title="Loading products" /> : null}
      {productsQuery.isError ? <StateBlock title="Could not load products" /> : null}
      {!productsQuery.isLoading && products.length === 0 ? <StateBlock title="No products yet" message="Product creation UI is planned after dashboard shell." /> : null}

      {products.length > 0 ? (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Product</th>
                <th>Price</th>
                <th>Stock</th>
                <th>Availability</th>
                <th>Attributes</th>
              </tr>
            </thead>
            <tbody>
              {products.map((product) => (
                <tr key={product.id}>
                  <td>
                    <strong>{product.name}</strong>
                    <span className="muted block">{product.description || product.id}</span>
                  </td>
                  <td>{formatMoney(product.price)}</td>
                  <td>{product.stockStatus.replaceAll('_', ' ')}</td>
                  <td>{product.isAvailable ? 'Available' : 'Hidden'}</td>
                  <td>{product.attributes?.slice(0, 3).map((attribute) => `${attribute.key}: ${attribute.value}`).join(', ') || '-'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : null}
    </section>
  );
}
