type Props = {
  title: string;
  message?: string;
};

export function StateBlock({ title, message }: Props) {
  return (
    <div className="state-block">
      <strong>{title}</strong>
      {message ? <p>{message}</p> : null}
    </div>
  );
}
