package com.admin.system.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageDto {
    private Long id;
    private Long senderId;
    private Long recipientId;
    private String content;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
