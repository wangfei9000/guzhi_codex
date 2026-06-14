package com.admin.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "report_template")
public class ReportTemplate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "template_name", nullable = false, length = 100)
    private String templateName;

    @Column(name = "template_content", nullable = false, columnDefinition = "TEXT")
    private String templateContent;

    @Column(nullable = false, length = 20)
    private String status = "启用";
}
