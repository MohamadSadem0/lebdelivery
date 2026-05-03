import { StateBlock } from '../../components/feedback/StateBlock';

type Props = {
  title: string;
  message: string;
};

export function PlaceholderPage({ title, message }: Props) {
  return (
    <section className="page-section">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Planned admin module</p>
          <h1>{title}</h1>
        </div>
      </div>
      <StateBlock title={`${title} placeholder`} message={message} />
    </section>
  );
}
