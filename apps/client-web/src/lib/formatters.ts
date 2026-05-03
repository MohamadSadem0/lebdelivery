import type { Money } from '@lebanon-platform/shared-types';

export function formatMoney(value?: Money | number) {
  if (value === undefined || value === null || value === '') {
    return '$0.00';
  }
  return `$${Number(value).toFixed(2)}`;
}

export function formatStatus(status?: string) {
  return status ? status.split('_').join(' ').toLowerCase() : '';
}

export function formatDateTime(value?: string) {
  if (!value) {
    return '';
  }
  return new Date(value).toLocaleString();
}
