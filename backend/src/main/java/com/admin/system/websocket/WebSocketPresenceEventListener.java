package com.admin.system.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class WebSocketPresenceEventListener {

    private final OnlineUserService onlineUserService;

    @EventListener
    public void handleConnected(SessionConnectedEvent event) {
        Principal principal = event.getUser();
        if (principal != null) {
            onlineUserService.markOnline(principal.getName());
        }
    }

    @EventListener
    public void handleDisconnected(SessionDisconnectEvent event) {
        Principal principal = event.getUser();
        if (principal != null) {
            onlineUserService.markOffline(principal.getName());
        }
    }
}
