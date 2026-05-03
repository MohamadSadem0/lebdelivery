import { create } from 'zustand';

type ProviderSelectionState = {
  selectedProviderId?: string;
  setSelectedProviderId: (providerId?: string) => void;
};

export const useProviderSelectionStore = create<ProviderSelectionState>((set) => ({
  selectedProviderId: undefined,
  setSelectedProviderId: (selectedProviderId) => set({ selectedProviderId })
}));
