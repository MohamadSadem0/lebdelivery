import { useQuery } from '@tanstack/react-query';
import { StateBlock } from '../../../components/feedback/StateBlock';
import { formatDate } from '../../../lib/formatters';
import { useStoreSelectionStore } from '../../stores/store/storeSelectionStore';
import { getStoreInventory } from '../api/inventoryApi';

export function InventoryPage() {
  const selectedStoreId = useStoreSelectionStore((state) => state.selectedStoreId);
  const inventoryQuery = useQuery({
    queryKey: ['store-inventory', selectedStoreId],
    queryFn: () => getStoreInventory(selectedStoreId as string),
    enabled: Boolean(selectedStoreId)
  });

  if (!selectedStoreId) {
    return <StateBlock title="Choose a store" message="Select a store from the top bar to inspect inventory." />;
  }

  const rows = inventoryQuery.data ?? [];

  return (
    <section className="page-section">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Stock control</p>
          <h1>Inventory</h1>
        </div>
      </div>

      {inventoryQuery.isLoading ? <StateBlock title="Loading inventory" /> : null}
      {inventoryQuery.isError ? <StateBlock title="Could not load inventory" /> : null}
      {!inventoryQuery.isLoading && rows.length === 0 ? <StateBlock title="No inventory rows" message="Inventory appears after products are created with stock." /> : null}

      {rows.length > 0 ? (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Product</th>
                <th>Variant</th>
                <th>Mode</th>
                <th>Quantity</th>
                <th>Reserved</th>
                <th>Low stock at</th>
                <th>Unit</th>
                <th>Updated</th>
              </tr>
            </thead>
            <tbody>
              {rows.map((item) => (
                <tr key={item.id}>
                  <td>{item.productId}</td>
                  <td>{item.productVariantId || '-'}</td>
                  <td>{item.inventoryMode.replaceAll('_', ' ')}</td>
                  <td>{item.quantity}</td>
                  <td>{item.reservedQuantity}</td>
                  <td>{item.lowStockThreshold}</td>
                  <td>{item.unitType || '-'}</td>
                  <td>{formatDate(item.updatedAt)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : null}
    </section>
  );
}
