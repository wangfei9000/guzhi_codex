package com.admin.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 仪表盘统计数据DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {
    /** 用户总数 */
    private long userCount;
    /** 项目总数 */
    private long projectCount;
    /** 文件总数 */
    private long fileCount;
    /** 未读通知数 */
    private long unreadNotificationCount;
}
