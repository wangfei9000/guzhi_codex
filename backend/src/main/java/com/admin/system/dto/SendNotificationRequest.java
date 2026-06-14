package com.admin.system.dto;

import lombok.Data;

/**
 * 发送通知请求
 */
@Data
public class SendNotificationRequest {
    /** 接收用户ID */
    private Long recipientId;
    /** 通知标题 */
    private String title;
    /** 通知内容 */
    private String content;
}
