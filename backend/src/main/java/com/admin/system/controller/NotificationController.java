package com.admin.system.controller;

import com.admin.system.common.ApiResponse;
import com.admin.system.common.PageResponse;
import com.admin.system.dto.SendNotificationRequest;
import com.admin.system.entity.SysNotification;
import com.admin.system.entity.SysUser;
import com.admin.system.repository.SysUserRepository;
import com.admin.system.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SysUserRepository userRepository;

    private Long resolveUserId(Principal principal) {
        if (principal == null) return 1L;
        String username = principal.getName();
        return userRepository.findByUsername(username)
                .map(SysUser::getId)
                .orElse(1L);
    }

    @GetMapping
    public ApiResponse<PageResponse<SysNotification>> listNotifications(
            Principal principal,
            @PageableDefault(size = 10) Pageable pageable) {
        Long userId = resolveUserId(principal);
        Page<SysNotification> page = notificationService.getUserNotifications(userId, pageable);
        return ApiResponse.success(PageResponse.of(page));
    }

    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnreadCount(Principal principal) {
        Long userId = resolveUserId(principal);
        return ApiResponse.success(notificationService.getUnreadCount(userId));
    }

    @PutMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(@PathVariable Long id, Principal principal) {
        Long userId = resolveUserId(principal);
        notificationService.markAsRead(id, userId);
        return ApiResponse.success("已标记为已读", null);
    }

    @PutMapping("/read-all")
    public ApiResponse<Void> markAllAsRead(Principal principal) {
        Long userId = resolveUserId(principal);
        notificationService.markAllAsRead(userId);
        return ApiResponse.success("全部已读", null);
    }

    @PostMapping("/send")
    public ApiResponse<SysNotification> sendNotification(
            @RequestBody SendNotificationRequest request,
            Principal principal) {
        Long senderId = resolveUserId(principal);
        SysNotification notification = notificationService.createNotification(
                request.getRecipientId(),
                senderId,
                request.getTitle(),
                request.getContent()
        );
        return ApiResponse.success("发送成功", notification);
    }
}
