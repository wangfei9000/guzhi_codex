package com.admin.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Data
@Component
@ConfigurationProperties(prefix = "app.ollama")
public class AssistantProperties {

    private String baseUrl = "http://127.0.0.1:11434";
    private String model = "llama3.2";
    private Duration timeout = Duration.ofSeconds(120);
}
