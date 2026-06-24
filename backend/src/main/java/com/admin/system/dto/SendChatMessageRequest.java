package com.admin.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SendChatMessageRequest {
    @NotNull(message = "请选择接收人")
    private Long recipientId;

    @NotBlank(message = "请输入消息内容")
    @Size(max = 2000, message = "消息内容不能超过2000个字符")
    private String content;
}
