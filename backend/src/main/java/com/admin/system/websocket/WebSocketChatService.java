package com.admin.system.websocket;

import com.admin.system.dto.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketChatService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendMessage(ChatMessageDto message) {
        messagingTemplate.convertAndSendToUser(
                String.valueOf(message.getRecipientId()),
                "/queue/chat/message",
                message
        );
        messagingTemplate.convertAndSendToUser(
                String.valueOf(message.getSenderId()),
                "/queue/chat/message",
                message
        );
    }
}
