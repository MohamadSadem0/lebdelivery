import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { getStores } from '../api/catalogApi';

export function StoresPage() {
  const stores = useQuery({ queryKey: ['stores'], queryFn: getStores });

  return (
    <section className="stack">
      <div className="page-header">
        <h1>Stores</h1>
        <p className="muted">Browse active local stores.</p>
      </div>
      {stores.isLoading ? <p className="muted">Loading stores...</p> : null}
      {stores.isError ? <p className="error-text">Stores are unavailable right now.</p> : null}
      <div className="card-grid">
        {stores.data?.map((store) => (
          <Link className="card" key={store.id} to={`/stores/${store.id}`}>
            <h2>{store.name}</h2>
            <p>{store.description ?? store.address ?? store.storeTypeCode}</p>
            <div className="button-row">
              <span className="badge">{store.storeTypeCode}</span>
              <span className={`badge ${store.openNow ? '' : 'badge-muted'}`}>{store.openNow ? 'Open now' : 'Closed'}</span>
            </div>
          </Link>
        ))}
      </div>
    </section>
  );
}
