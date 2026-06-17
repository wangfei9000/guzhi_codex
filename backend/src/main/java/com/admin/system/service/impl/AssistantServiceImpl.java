package com.admin.system.service.impl;

import com.admin.system.dto.AssistantGenerateRequest;
import com.admin.system.dto.AssistantGenerateResponse;
import com.admin.system.service.AssistantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssistantServiceImpl implements AssistantService {

    private final OllamaAssistantClient ollamaAssistantClient;

    @Override
    public AssistantGenerateResponse generate(AssistantGenerateRequest request) {
        return ollamaAssistantClient.chat(request.getPrompt().trim());
    }
}
