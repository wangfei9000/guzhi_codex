package com.admin.system.websocket;

import com.admin.system.entity.SysNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(Long userId, SysNotification notification) {
        Map<String, Object> payload = Map.of(
                "id", notification.getId(),
                "title", notification.getTitle(),
                "content", notification.getContent(),
                "isRead", notification.getIsRead(),
                "createdAt", notification.getCreatedAt().toString()
        );

        messagingTemplate.convertAndSendToUser(
                String.valueOf(userId),
                "/queue/notification",
                payload
        );

        log.info("WebSocket notification sent to user {}: {}", userId, notification.getTitle());
    }

    public void sendBroadcast(String message) {
        messagingTemplate.convertAndSend("/topic/broadcast", Map.of("message", message));
        log.info("Broadcast message sent: {}", message);
    }
}
