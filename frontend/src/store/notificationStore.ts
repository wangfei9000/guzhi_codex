import { create } from 'zustand';
import type { NotificationItem } from '@/api/types';

interface NotificationState {
  notifications: NotificationItem[];
  unreadCount: number;
  setNotifications: (list: NotificationItem[]) => void;
  addNotification: (item: NotificationItem) => void;
  markAsRead: (id: number) => void;
  markAllAsRead: () => void;
  incrementUnread: () => void;
}

export const useNotificationStore = create<NotificationState>()((set, get) => ({
  notifications: [],
  unreadCount: 0,

  setNotifications: (list) => {
    set({ notifications: list, unreadCount: list.filter((n) => !n.isRead).length });
  },

  addNotification: (item) => {
    set((state) => ({
      notifications: [item, ...state.notifications],
      unreadCount: state.unreadCount + 1,
    }));
  },

  markAsRead: (id) => {
    set((state) => ({
      notifications: state.notifications.map((n) =>
        n.id === id ? { ...n, isRead: true } : n
      ),
      unreadCount: Math.max(0, state.unreadCount - 1),
    }));
  },

  markAllAsRead: () => {
    set((state) => ({
      notifications: state.notifications.map((n) => ({ ...n, isRead: true })),
      unreadCount: 0,
    }));
  },

  incrementUnread: () => {
    set((state) => ({ unreadCount: state.unreadCount + 1 }));
  },
}));
