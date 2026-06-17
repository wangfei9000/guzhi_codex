package com.admin.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssistantGenerateResponse {

    private String model;
    private String response;
    private List<Map<String, Object>> toolCalls;
}
