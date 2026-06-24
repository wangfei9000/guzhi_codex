package com.admin.system.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatUserDto {
    private Long id;
    private String username;
    private String nickname;
    private Boolean online;
    private Long unreadCount;
    private String lastMessage;
    private Long lastSenderId;
    private LocalDateTime lastMessageTime;
}
