import { useQuery } from '@tanstack/react-query';
import { getMyStores } from '../api/storesApi';
import { useStoreSelectionStore } from '../store/storeSelectionStore';
import { StateBlock } from '../../../components/feedback/StateBlock';

export function StoresPage() {
  const storesQuery = useQuery({ queryKey: ['my-stores'], queryFn: getMyStores });
  const { selectedStoreId, setSelectedStoreId } = useStoreSelectionStore();
  const stores = storesQuery.data ?? [];

  return (
    <section className="page-section">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Store system</p>
          <h1>My Stores</h1>
        </div>
      </div>

      {storesQuery.isLoading ? <StateBlock title="Loading stores" /> : null}
      {storesQuery.isError ? <StateBlock title="Could not load stores" message="Check the backend connection and your store-owner permissions." /> : null}
      {!storesQuery.isLoading && stores.length === 0 ? (
        <StateBlock title="No stores yet" message="Store onboarding is available through the API. Dashboard creation form comes next." />
      ) : null}

      <div className="store-grid">
        {stores.map((store) => (
          <article className={selectedStoreId === store.id ? 'store-card selected' : 'store-card'} key={store.id}>
            <div>
              <div className="row-between">
                <h2>{store.name}</h2>
                <span className={`status-pill ${store.status.toLowerCase()}`}>{store.status.replaceAll('_', ' ')}</span>
              </div>
              <p className="muted">{store.description || store.address || 'No description yet'}</p>
            </div>
            <dl className="metadata-grid">
              <div>
                <dt>Type</dt>
                <dd>{store.storeTypeCode.replaceAll('_', ' ')}</dd>
              </div>
              <div>
                <dt>Delivery</dt>
                <dd>{store.deliveryMode.replaceAll('_', ' ')}</dd>
              </div>
              <div>
                <dt>Phone</dt>
                <dd>{store.phone || '-'}</dd>
              </div>
              <div>
                <dt>Rating</dt>
                <dd>{store.ratingAverage ?? '0'} ({store.ratingCount ?? 0})</dd>
              </div>
            </dl>
            <button className="primary-button" type="button" onClick={() => setSelectedStoreId(store.id)}>
              Use this store
            </button>
          </article>
        ))}
      </div>
    </section>
  );
}
