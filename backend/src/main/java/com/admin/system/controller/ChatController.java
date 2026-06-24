package com.admin.system.controller;

import com.admin.system.common.ApiResponse;
import com.admin.system.common.PageResponse;
import com.admin.system.dto.ChatMessageDto;
import com.admin.system.dto.ChatUserDto;
import com.admin.system.dto.SendChatMessageRequest;
import com.admin.system.entity.SysUser;
import com.admin.system.exception.BusinessException;
import com.admin.system.repository.SysUserRepository;
import com.admin.system.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SysUserRepository userRepository;

    @GetMapping("/users")
    public ApiResponse<List<ChatUserDto>> listUsers(Principal principal) {
        Long currentUserId = resolveUserId(principal);
        return ApiResponse.success(chatService.listUsers(currentUserId));
    }

    @GetMapping("/messages")
    public ApiResponse<PageResponse<ChatMessageDto>> listMessages(
            @RequestParam Long peerId,
            Principal principal,
            @PageableDefault(size = 50) Pageable pageable) {
        Long currentUserId = resolveUserId(principal);
        Page<ChatMessageDto> page = chatService.listMessages(currentUserId, peerId, pageable);
        return ApiResponse.success(PageResponse.of(page));
    }

    @PostMapping("/messages")
    public ApiResponse<ChatMessageDto> sendMessage(
            @Valid @RequestBody SendChatMessageRequest request,
            Principal principal) {
        Long currentUserId = resolveUserId(principal);
        ChatMessageDto message = chatService.sendMessage(currentUserId, request);
        return ApiResponse.success("发送成功", message);
    }

    @PutMapping("/messages/read")
    public ApiResponse<Void> markConversationRead(@RequestParam Long peerId, Principal principal) {
        Long currentUserId = resolveUserId(principal);
        chatService.markConversationRead(currentUserId, peerId);
        return ApiResponse.success("已读", null);
    }

    private Long resolveUserId(Principal principal) {
        if (principal == null) {
            throw new BusinessException("用户未登录");
        }
        return userRepository.findByUsername(principal.getName())
                .map(SysUser::getId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
    }
}
