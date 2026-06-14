package com.admin.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_file_record")
public class FileRecord extends BaseEntity {

    /** 主键ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 原始文件名 */
    @Column(nullable = false, length = 255)
    private String originalName;

    /** 存储文件名 */
    @Column(nullable = false, unique = true, length = 255)
    private String storedName;

    /** 文件路径 */
    @Column(nullable = false, length = 500)
    private String filePath;

    /** 文件大小 */
    @Column(nullable = false)
    private Long fileSize;

    /** 文件类型 */
    @Column(length = 100)
    private String contentType;

    /** 上传用户ID */
    private Long uploadUserId;
}
