package com.admin.system.service;

import com.admin.system.entity.SysNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    Page<SysNotification> getUserNotifications(Long userId, Pageable pageable);
    long getUnreadCount(Long userId);
    void markAsRead(Long id, Long userId);
    void markAllAsRead(Long userId);
    SysNotification createNotification(Long userId, Long senderId, String title, String content);
}
