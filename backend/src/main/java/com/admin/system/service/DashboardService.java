package com.admin.system.service;

import com.admin.system.dto.DashboardStatsDto;
import com.admin.system.repository.FileRecordRepository;
import com.admin.system.repository.ProjectRepository;
import com.admin.system.repository.SysNotificationRepository;
import com.admin.system.repository.SysUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SysUserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final FileRecordRepository fileRecordRepository;
    private final SysNotificationRepository notificationRepository;

    public DashboardStatsDto getStats(Long userId) {
        long userCount = userRepository.count();
        long projectCount = projectRepository.count();
        long fileCount = fileRecordRepository.count();
        long unreadCount = notificationRepository.countByUserIdAndIsReadFalse(userId);
        return new DashboardStatsDto(userCount, projectCount, fileCount, unreadCount);
    }
}
