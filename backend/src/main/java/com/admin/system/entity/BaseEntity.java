package com.admin.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    /** 创建时间 */
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
