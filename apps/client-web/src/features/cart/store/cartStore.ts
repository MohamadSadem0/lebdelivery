import { create } from 'zustand';
import type { CartItem, Money, Product, Store } from '@lebanon-platform/shared-types';

type CartState = {
  storeId?: string;
  storeName?: string;
  items: CartItem[];
  addProduct: (store: Pick<Store, 'id' | 'name'>, product: Product) => void;
  removeProduct: (productId: string, productVariantId?: string) => void;
  increase: (productId: string, productVariantId?: string) => void;
  decrease: (productId: string, productVariantId?: string) => void;
  clear: () => void;
  subtotal: () => number;
};

function sameLine(item: CartItem, productId: string, productVariantId?: string) {
  return item.productId === productId && (item.productVariantId ?? '') === (productVariantId ?? '');
}

function asMoney(value: Money) {
  return Number(value);
}

export const useCartStore = create<CartState>((set, get) => ({
  items: [],
  addProduct(store, product) {
    const state = get();
    if (state.storeId && state.storeId !== store.id) {
      const replace = window.confirm('Your cart has items from another store. Replace cart with this store?');
      if (!replace) {
        return;
      }
      set({ storeId: store.id, storeName: store.name, items: [] });
    }

    const nextStoreId = get().storeId ?? store.id;
    const nextStoreName = get().storeName ?? store.name;
    const existing = get().items.find((item) => sameLine(item, product.id));
    if (existing) {
      set({
        storeId: nextStoreId,
        storeName: nextStoreName,
        items: get().items.map((item) => (sameLine(item, product.id) ? { ...item, quantity: item.quantity + 1 } : item))
      });
      return;
    }

    set({
      storeId: nextStoreId,
      storeName: nextStoreName,
      items: [
        ...get().items,
        {
          productId: product.id,
          storeId: store.id,
          storeName: store.name,
          name: product.name,
          imageUrl: product.imageUrl,
          unitPrice: product.price,
          quantity: 1
        }
      ]
    });
  },
  removeProduct(productId, productVariantId) {
    const items = get().items.filter((item) => !sameLine(item, productId, productVariantId));
    set({ items, storeId: items.length === 0 ? undefined : get().storeId, storeName: items.length === 0 ? undefined : get().storeName });
  },
  increase(productId, productVariantId) {
    set({ items: get().items.map((item) => (sameLine(item, productId, productVariantId) ? { ...item, quantity: item.quantity + 1 } : item)) });
  },
  decrease(productId, productVariantId) {
    const items = get()
      .items.map((item) => (sameLine(item, productId, productVariantId) ? { ...item, quantity: Math.max(0, item.quantity - 1) } : item))
      .filter((item) => item.quantity > 0);
    set({ items, storeId: items.length === 0 ? undefined : get().storeId, storeName: items.length === 0 ? undefined : get().storeName });
  },
  clear() {
    set({ storeId: undefined, storeName: undefined, items: [] });
  },
  subtotal() {
    return get().items.reduce((sum, item) => sum + asMoney(item.unitPrice) * item.quantity, 0);
  }
}));
