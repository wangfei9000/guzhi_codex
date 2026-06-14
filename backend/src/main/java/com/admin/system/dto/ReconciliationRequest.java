package com.admin.system.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReconciliationRequest {

    private LocalDate startTime;

    private LocalDate endTime;

    private String remark;
}
