import { create } from 'zustand';
import type { ChatMessage } from '@/api/types';

interface ChatState {
  lastMessage: ChatMessage | null;
  presenceByUserId: Record<number, boolean>;
  addChatMessage: (message: ChatMessage) => void;
  setPresence: (userId: number, online: boolean) => void;
}

export const useChatStore = create<ChatState>()((set) => ({
  lastMessage: null,
  presenceByUserId: {},

  addChatMessage: (lastMessage) => set({ lastMessage }),

  setPresence: (userId, online) => {
    set((state) => ({
      presenceByUserId: {
        ...state.presenceByUserId,
        [userId]: online,
      },
    }));
  },
}));

