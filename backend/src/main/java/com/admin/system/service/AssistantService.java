package com.admin.system.service;

import com.admin.system.dto.AssistantGenerateRequest;
import com.admin.system.dto.AssistantGenerateResponse;

public interface AssistantService {

    AssistantGenerateResponse generate(AssistantGenerateRequest request);
}
