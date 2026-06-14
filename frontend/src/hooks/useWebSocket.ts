import { useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import { useNotificationStore } from '@/store/notificationStore';
import { useAuthStore } from '@/store/authStore';
import { AUTH_TOKEN_KEY } from '@/utils/constants';
import type { NotificationItem } from '@/api/types';

export function useWebSocket() {
  const clientRef = useRef<Client | null>(null);
  const token = useAuthStore((s) => s.token);
  const addNotification = useNotificationStore((s) => s.addNotification);

  useEffect(() => {
    if (!token) return;

    const wsProtocol = window.location.protocol === 'https:' ? 'wss' : 'ws';

    const client = new Client({
      brokerURL: `${wsProtocol}://${window.location.host}/ws`,
      connectHeaders: {
        Authorization: `Bearer ${localStorage.getItem(AUTH_TOKEN_KEY)}`,
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      debug: (msg) => {
        if (import.meta.env.DEV) console.debug('[STOMP]', msg);
      },
    });

    client.onConnect = () => {
      client.subscribe('/user/queue/notification', (message) => {
        try {
          const data = JSON.parse(message.body) as NotificationItem;
          addNotification(data);
        } catch {
          console.warn('Failed to parse WebSocket message');
        }
      });

      client.subscribe('/topic/broadcast', (message) => {
        try {
          const data = JSON.parse(message.body);
          if (data.message) {
            addNotification({
              id: Date.now(),
              userId: 0,
              senderId: null,
              title: '系统广播',
              content: data.message,
              isRead: false,
              createdAt: new Date().toISOString(),
            });
          }
        } catch { /* ignore */ }
      });
    };

    client.activate();
    clientRef.current = client;

    return () => {
      client.deactivate();
    };
  }, [token, addNotification]);
}
