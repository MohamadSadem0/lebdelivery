import { useQuery } from '@tanstack/react-query';
import { getAdminDeliveries, getAdminDrivers, getAdminOrders, getAdminPayments, getAdminReceipts, getAdminStores, getAdminUsers } from '../../admin/api/adminApi';
import { StateBlock } from '../../../components/feedback/StateBlock';
import { formatMoney } from '../../../lib/formatters';

export function OverviewPage() {
  const usersQuery = useQuery({ queryKey: ['admin-users'], queryFn: getAdminUsers });
  const storesQuery = useQuery({ queryKey: ['admin-stores'], queryFn: getAdminStores });
  const driversQuery = useQuery({ queryKey: ['admin-drivers'], queryFn: getAdminDrivers });
  const ordersQuery = useQuery({ queryKey: ['admin-orders'], queryFn: getAdminOrders });
  const deliveriesQuery = useQuery({ queryKey: ['admin-deliveries'], queryFn: getAdminDeliveries });
  const paymentsQuery = useQuery({ queryKey: ['admin-payments'], queryFn: getAdminPayments });
  const receiptsQuery = useQuery({ queryKey: ['admin-receipts'], queryFn: getAdminReceipts });

  const users = usersQuery.data ?? [];
  const stores = storesQuery.data ?? [];
  const drivers = driversQuery.data ?? [];
  const orders = ordersQuery.data ?? [];
  const deliveries = deliveriesQuery.data ?? [];
  const payments = paymentsQuery.data ?? [];
  const receipts = receiptsQuery.data ?? [];
  const totalReceipts = receipts.reduce((sum, receipt) => sum + Number(receipt.totalAmount || 0), 0);

  return (
    <section className="page-section">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Marketplace control</p>
          <h1>Overview</h1>
        </div>
      </div>

      {[usersQuery, storesQuery, driversQuery, ordersQuery, deliveriesQuery, paymentsQuery, receiptsQuery].some((query) => query.isError) ? (
        <StateBlock title="Some admin data could not load" message="Confirm your admin token and backend health." />
      ) : null}

      <div className="metric-grid">
        <Metric title="Users" value={users.length} detail={`${users.filter((user) => user.status === 'SUSPENDED').length} suspended`} />
        <Metric title="Stores" value={stores.length} detail={`${stores.filter((store) => store.status === 'PENDING_APPROVAL').length} pending approval`} />
        <Metric title="Drivers" value={drivers.length} detail={`${drivers.filter((driver) => driver.status === 'PENDING_APPROVAL').length} pending approval`} />
        <Metric title="Orders" value={orders.length} detail={`${orders.filter((order) => order.status === 'ISSUE_REPORTED').length} issues reported`} />
        <Metric title="Deliveries" value={deliveries.length} detail={`${deliveries.filter((delivery) => delivery.status === 'FAILED_DELIVERY').length} failed`} />
        <Metric title="Cash" value={payments.filter((payment) => payment.status === 'COLLECTED').length} detail={`${payments.filter((payment) => payment.cashMismatch).length} mismatches`} />
        <Metric title="Receipts" value={formatMoney(totalReceipts)} detail={`${receipts.length} receipt records`} />
      </div>
    </section>
  );
}

function Metric({ title, value, detail }: { title: string; value: string | number; detail: string }) {
  return (
    <article className="metric-card">
      <span className="muted">{title}</span>
      <strong>{value}</strong>
      <p>{detail}</p>
    </article>
  );
}
