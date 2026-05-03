import { Link } from 'react-router-dom';

export function HomePage() {
  const sections = ['Nearby stores', 'Fastest delivery', 'Open now', 'Pharmacy now', 'Quick essentials', 'Send package', 'Repair and laundry', 'Urgent needs', 'Reorder again'];

  return (
    <section className="stack">
      <div className="page-header">
        <p className="eyebrow">Lebanon Local Commerce</p>
        <h1>Order from nearby stores and services</h1>
        <p className="muted">A client web foundation for browsing, COD checkout, tracking, receipts, and support.</p>
      </div>
      <div className="quick-grid">
        {sections.map((section) => (
          <Link key={section} className="quick-tile" to="/stores">{section}</Link>
        ))}
      </div>
    </section>
  );
}
