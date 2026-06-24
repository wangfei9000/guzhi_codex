package com.admin.system.service;

import com.admin.system.dto.ChatMessageDto;
import com.admin.system.dto.ChatUserDto;
import com.admin.system.dto.SendChatMessageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatService {
    List<ChatUserDto> listUsers(Long currentUserId);

    Page<ChatMessageDto> listMessages(Long currentUserId, Long peerId, Pageable pageable);

    ChatMessageDto sendMessage(Long senderId, SendChatMessageRequest request);

    void markConversationRead(Long currentUserId, Long peerId);
}
