package com.admin.system.service.impl;

import com.admin.system.common.ChatConstants;
import com.admin.system.entity.SysNotification;
import com.admin.system.exception.BusinessException;
import com.admin.system.repository.SysNotificationRepository;
import com.admin.system.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final SysNotificationRepository notificationRepository;

    @Override
    public Page<SysNotification> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdAndTitleNotOrderByCreatedAtDesc(userId, ChatConstants.CHAT_TITLE, pageable);
    }

    @Override
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndTitleNotAndIsReadFalse(userId, ChatConstants.CHAT_TITLE);
    }

    @Override
    @Transactional
    public void markAsRead(Long id, Long userId) {
        SysNotification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("通知不存在"));
        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此通知");
        }
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        Page<SysNotification> unread = notificationRepository.findByUserIdAndTitleNotOrderByCreatedAtDesc(
                userId,
                ChatConstants.CHAT_TITLE,
                Pageable.unpaged()
        );
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread.getContent());
    }

    @Override
    @Transactional
    public SysNotification createNotification(Long userId, Long senderId, String title, String content) {
        SysNotification notification = new SysNotification();
        notification.setUserId(userId);
        notification.setSenderId(senderId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setIsRead(false);
        return notificationRepository.save(notification);
    }
}
