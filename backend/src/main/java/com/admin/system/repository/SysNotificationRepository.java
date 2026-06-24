package com.admin.system.repository;

import com.admin.system.entity.SysNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SysNotificationRepository extends JpaRepository<SysNotification, Long> {
    Page<SysNotification> findByUserIdAndTitleNotOrderByCreatedAtDesc(Long userId, String title, Pageable pageable);

    long countByUserIdAndTitleNotAndIsReadFalse(Long userId, String title);

    @Query("""
            SELECT n FROM SysNotification n
            WHERE n.title = :title
              AND ((n.userId = :userId AND n.senderId = :peerId)
                   OR (n.userId = :peerId AND n.senderId = :userId))
            ORDER BY n.createdAt DESC
            """)
    Page<SysNotification> findChatConversation(
            @Param("userId") Long userId,
            @Param("peerId") Long peerId,
            @Param("title") String title,
            Pageable pageable);

    @Query("""
            SELECT n FROM SysNotification n
            WHERE n.title = :title
              AND (n.userId = :userId OR n.senderId = :userId)
            ORDER BY n.createdAt DESC
            """)
    List<SysNotification> findChatMessagesForUser(@Param("userId") Long userId, @Param("title") String title);

    long countByUserIdAndSenderIdAndTitleAndIsReadFalse(Long userId, Long senderId, String title);

    @Modifying
    @Query("""
            UPDATE SysNotification n
            SET n.isRead = true, n.updatedAt = :readAt
            WHERE n.userId = :userId
              AND n.senderId = :peerId
              AND n.title = :title
              AND n.isRead = false
            """)
    int markChatConversationRead(
            @Param("userId") Long userId,
            @Param("peerId") Long peerId,
            @Param("title") String title,
            @Param("readAt") LocalDateTime readAt);
}
