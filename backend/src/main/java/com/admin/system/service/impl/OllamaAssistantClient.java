package com.admin.system.service.impl;

import com.admin.system.config.AssistantProperties;
import com.admin.system.dto.AssistantGenerateResponse;
import com.admin.system.exception.BusinessException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class OllamaAssistantClient {

    private final AssistantProperties properties;
    private final RestClient restClient;

    public OllamaAssistantClient(AssistantProperties properties) {
        this.properties = properties;
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        int timeoutMillis = toMillis(properties.getTimeout());
        requestFactory.setConnectTimeout(timeoutMillis);
        requestFactory.setReadTimeout(timeoutMillis);
        this.restClient = RestClient.builder()
                .baseUrl(trimTrailingSlash(properties.getBaseUrl()))
                .requestFactory(requestFactory)
                .build();
    }

    public AssistantGenerateResponse chat(String prompt) {
        OllamaChatRequest request = new OllamaChatRequest(
                properties.getModel(),
                List.of(new OllamaMessage("user", prompt, null)),
                false
        );

        try {
            OllamaChatResponse response = restClient.post()
                    .uri("/api/chat")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(OllamaChatResponse.class);

            if (response == null || response.getMessage() == null) {
                throw new BusinessException(502, "本地模型没有返回内容");
            }

            OllamaMessage message = response.getMessage();
            return new AssistantGenerateResponse(
                    response.getModel() == null ? properties.getModel() : response.getModel(),
                    message.getContent() == null ? "" : message.getContent(),
                    message.getToolCalls()
            );
        } catch (RestClientResponseException e) {
            log.warn("Ollama request failed: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new BusinessException(502, "调用本地 Ollama 失败，请确认模型 " + properties.getModel() + " 可用");
        } catch (RestClientException e) {
            log.warn("Ollama request failed", e);
            throw new BusinessException(502, "调用本地 Ollama 失败，请确认 Ollama 已启动");
        }
    }

    private int toMillis(Duration duration) {
        long millis = duration == null ? Duration.ofSeconds(120).toMillis() : duration.toMillis();
        return (int) Math.min(Math.max(millis, 1), Integer.MAX_VALUE);
    }

    private String trimTrailingSlash(String value) {
        if (value == null || value.isBlank()) {
            return "http://127.0.0.1:11434";
        }
        return value.replaceAll("/+$", "");
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class OllamaChatRequest {
        private String model;
        private List<OllamaMessage> messages;
        private boolean stream;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class OllamaMessage {
        private String role;
        private String content;
        @JsonProperty("tool_calls")
        private List<Map<String, Object>> toolCalls;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class OllamaChatResponse {
        private String model;
        private OllamaMessage message;
    }
}
