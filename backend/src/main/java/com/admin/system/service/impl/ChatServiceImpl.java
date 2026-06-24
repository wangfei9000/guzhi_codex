package com.admin.system.service.impl;

import com.admin.system.common.ChatConstants;
import com.admin.system.dto.ChatMessageDto;
import com.admin.system.dto.ChatUserDto;
import com.admin.system.dto.SendChatMessageRequest;
import com.admin.system.entity.SysNotification;
import com.admin.system.entity.SysUser;
import com.admin.system.exception.BusinessException;
import com.admin.system.repository.SysNotificationRepository;
import com.admin.system.repository.SysUserRepository;
import com.admin.system.service.ChatService;
import com.admin.system.websocket.OnlineUserService;
import com.admin.system.websocket.WebSocketChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final SysNotificationRepository notificationRepository;
    private final SysUserRepository userRepository;
    private final OnlineUserService onlineUserService;
    private final WebSocketChatService webSocketChatService;

    @Override
    public List<ChatUserDto> listUsers(Long currentUserId) {
        Map<Long, SysNotification> lastMessageByPeer = new HashMap<>();
        for (SysNotification message : notificationRepository.findChatMessagesForUser(currentUserId, ChatConstants.CHAT_TITLE)) {
            Long peerId = resolvePeerId(message, currentUserId);
            if (peerId != null) {
                lastMessageByPeer.putIfAbsent(peerId, message);
            }
        }

        return userRepository.findByStatusOrderByUsernameAsc(1).stream()
                .map(user -> toUserDto(user, currentUserId, lastMessageByPeer.get(user.getId())))
                .sorted((left, right) -> {
                    int onlineCompare = Boolean.compare(Boolean.TRUE.equals(right.getOnline()), Boolean.TRUE.equals(left.getOnline()));
                    if (onlineCompare != 0) {
                        return onlineCompare;
                    }
                    LocalDateTime leftTime = left.getLastMessageTime();
                    LocalDateTime rightTime = right.getLastMessageTime();
                    if (leftTime != null || rightTime != null) {
                        if (leftTime == null) return 1;
                        if (rightTime == null) return -1;
                        int timeCompare = rightTime.compareTo(leftTime);
                        if (timeCompare != 0) {
                            return timeCompare;
                        }
                    }
                    return displayName(left).compareToIgnoreCase(displayName(right));
                })
                .toList();
    }

    @Override
    public Page<ChatMessageDto> listMessages(Long currentUserId, Long peerId, Pageable pageable) {
        ensureUserExists(peerId);
        return notificationRepository.findChatConversation(
                currentUserId,
                peerId,
                ChatConstants.CHAT_TITLE,
                pageable
        ).map(this::toMessageDto);
    }

    @Override
    @Transactional
    public ChatMessageDto sendMessage(Long senderId, SendChatMessageRequest request) {
        if (senderId.equals(request.getRecipientId())) {
            throw new BusinessException("不能给自己发送消息");
        }
        ensureUserExists(request.getRecipientId());

        String content = request.getContent() == null ? "" : request.getContent().trim();
        if (!StringUtils.hasText(content)) {
            throw new BusinessException("请输入消息内容");
        }

        SysNotification message = new SysNotification();
        message.setUserId(request.getRecipientId());
        message.setSenderId(senderId);
        message.setTitle(ChatConstants.CHAT_TITLE);
        message.setContent(content);
        message.setIsRead(false);

        ChatMessageDto dto = toMessageDto(notificationRepository.save(message));
        webSocketChatService.sendMessage(dto);
        return dto;
    }

    @Override
    @Transactional
    public void markConversationRead(Long currentUserId, Long peerId) {
        ensureUserExists(peerId);
        notificationRepository.markChatConversationRead(
                currentUserId,
                peerId,
                ChatConstants.CHAT_TITLE,
                LocalDateTime.now()
        );
    }

    private ChatUserDto toUserDto(SysUser user, Long currentUserId, SysNotification lastMessage) {
        ChatUserDto dto = new ChatUserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        dto.setOnline(onlineUserService.isOnline(user.getId()));

        if (user.getId().equals(currentUserId)) {
            dto.setUnreadCount(0L);
        } else {
            dto.setUnreadCount(notificationRepository.countByUserIdAndSenderIdAndTitleAndIsReadFalse(
                    currentUserId,
                    user.getId(),
                    ChatConstants.CHAT_TITLE
            ));
        }

        if (lastMessage != null) {
            dto.setLastMessage(lastMessage.getContent());
            dto.setLastSenderId(lastMessage.getSenderId());
            dto.setLastMessageTime(lastMessage.getCreatedAt());
        }

        return dto;
    }

    private ChatMessageDto toMessageDto(SysNotification message) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setId(message.getId());
        dto.setSenderId(message.getSenderId());
        dto.setRecipientId(message.getUserId());
        dto.setContent(message.getContent());
        dto.setIsRead(message.getIsRead());
        dto.setCreatedAt(message.getCreatedAt());
        return dto;
    }

    private Long resolvePeerId(SysNotification message, Long currentUserId) {
        if (message.getUserId().equals(currentUserId)) {
            return message.getSenderId();
        }
        if (message.getSenderId() != null && message.getSenderId().equals(currentUserId)) {
            return message.getUserId();
        }
        return null;
    }

    private void ensureUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new BusinessException("用户不存在");
        }
    }

    private String displayName(ChatUserDto user) {
        return StringUtils.hasText(user.getNickname()) ? user.getNickname() : user.getUsername();
    }
}
