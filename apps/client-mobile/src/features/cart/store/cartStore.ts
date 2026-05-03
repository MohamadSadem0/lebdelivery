import { create } from 'zustand';
import type { CartItem } from '@lebanon-platform/shared-types';

type CartState = {
  storeId?: string;
  storeName?: string;
  items: CartItem[];
  addItem: (item: CartItem) => void;
  removeItem: (productId: string, productVariantId?: string) => void;
  increase: (productId: string, productVariantId?: string) => void;
  decrease: (productId: string, productVariantId?: string) => void;
  clear: () => void;
  subtotal: () => number;
};

function sameLine(left: CartItem, productId: string, productVariantId?: string) {
  return left.productId === productId && left.productVariantId === productVariantId;
}

export const useCartStore = create<CartState>((set, get) => ({
  storeId: undefined,
  storeName: undefined,
  items: [],
  addItem(item) {
    const state = get();

    if (state.storeId && state.storeId !== item.storeId) {
      // MVP supports one store per cart. Future UI should ask: replace cart or cancel.
      set({ storeId: item.storeId, storeName: item.storeName, items: [{ ...item, quantity: 1 }] });
      return;
    }

    const existing = state.items.find((cartItem) => sameLine(cartItem, item.productId, item.productVariantId));
    if (existing) {
      set({
        storeId: item.storeId,
        storeName: item.storeName,
        items: state.items.map((cartItem) =>
          sameLine(cartItem, item.productId, item.productVariantId)
            ? { ...cartItem, quantity: cartItem.quantity + item.quantity }
            : cartItem
        )
      });
      return;
    }

    set({ storeId: item.storeId, storeName: item.storeName, items: [...state.items, item] });
  },
  removeItem(productId, productVariantId) {
    const items = get().items.filter((item) => !sameLine(item, productId, productVariantId));
    set({ items, storeId: items[0]?.storeId, storeName: items[0]?.storeName });
  },
  increase(productId, productVariantId) {
    set({
      items: get().items.map((item) =>
        sameLine(item, productId, productVariantId) ? { ...item, quantity: item.quantity + 1 } : item
      )
    });
  },
  decrease(productId, productVariantId) {
    const items = get().items
      .map((item) => (sameLine(item, productId, productVariantId) ? { ...item, quantity: item.quantity - 1 } : item))
      .filter((item) => item.quantity > 0);
    set({ items, storeId: items[0]?.storeId, storeName: items[0]?.storeName });
  },
  clear() {
    set({ storeId: undefined, storeName: undefined, items: [] });
  },
  subtotal() {
    return get().items.reduce((sum, item) => sum + Number(item.unitPrice) * item.quantity, 0);
  }
}));
