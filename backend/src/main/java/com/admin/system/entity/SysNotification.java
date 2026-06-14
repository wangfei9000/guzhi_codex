package com.admin.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通知消息实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_notification")
public class SysNotification extends BaseEntity {

    /** 主键ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 接收用户ID */
    @Column(nullable = false)
    private Long userId;

    /** 发送用户ID */
    private Long senderId;

    /** 通知标题 */
    @Column(nullable = false, length = 200)
    private String title;

    /** 通知内容 */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** 是否已读 */
    @Column(nullable = false)
    private Boolean isRead = false;
}
