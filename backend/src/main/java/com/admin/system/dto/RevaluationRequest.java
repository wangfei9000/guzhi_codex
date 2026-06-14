package com.admin.system.dto;

import lombok.Data;

import java.util.List;

@Data
public class RevaluationRequest {
    private List<String> projectCodes;
    private String remark;
}
