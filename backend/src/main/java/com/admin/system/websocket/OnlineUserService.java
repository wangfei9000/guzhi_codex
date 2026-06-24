package com.admin.system.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class OnlineUserService {

    private final SimpMessagingTemplate messagingTemplate;
    private final Map<Long, Integer> connectionCounts = new ConcurrentHashMap<>();

    public void markOnline(String userIdText) {
        Long userId = parseUserId(userIdText);
        if (userId == null) {
            return;
        }

        int count = connectionCounts.merge(userId, 1, Integer::sum);
        if (count == 1) {
            publishPresence(userId, true);
        }
    }

    public void markOffline(String userIdText) {
        Long userId = parseUserId(userIdText);
        if (userId == null) {
            return;
        }

        final Long resolvedUserId = userId;
        connectionCounts.computeIfPresent(resolvedUserId, (id, count) -> {
            int next = count - 1;
            if (next <= 0) {
                publishPresence(id, false);
                return null;
            }
            return next;
        });
    }

    public boolean isOnline(Long userId) {
        return connectionCounts.getOrDefault(userId, 0) > 0;
    }

    private void publishPresence(Long userId, boolean online) {
        messagingTemplate.convertAndSend(
                "/topic/chat/presence",
                Map.of("userId", userId, "online", online)
        );
    }

    private Long parseUserId(String userIdText) {
        try {
            return Long.valueOf(userIdText);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
