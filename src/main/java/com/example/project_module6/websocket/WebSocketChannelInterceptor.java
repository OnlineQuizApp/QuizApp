package com.example.project_module6.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class WebSocketChannelInterceptor  implements ChannelInterceptor {



    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            Authentication auth = (Authentication) accessor.getSessionAttributes().get("auth");
            if (auth != null) {
                // Gắn user vào WebSocket session để dùng được SecurityContextHolder
                accessor.setUser(auth);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        return message;
    }
}
