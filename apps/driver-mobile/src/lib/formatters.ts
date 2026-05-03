export function formatMoney(amount: string | number, currency = 'USD') {
  const value = typeof amount === 'string' ? Number(amount) : amount;
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency
  }).format(value);
}
