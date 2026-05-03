import { create } from 'zustand';

type StoreSelectionState = {
  selectedStoreId?: string;
  setSelectedStoreId: (storeId?: string) => void;
};

export const useStoreSelectionStore = create<StoreSelectionState>((set) => ({
  selectedStoreId: undefined,
  setSelectedStoreId: (selectedStoreId) => set({ selectedStoreId })
}));
