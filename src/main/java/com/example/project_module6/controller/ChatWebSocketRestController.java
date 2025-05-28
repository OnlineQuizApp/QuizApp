    package com.example.project_module6.controller;


    import com.example.project_module6.dto.ChatMessage;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.messaging.handler.annotation.MessageMapping;
    import org.springframework.messaging.simp.SimpMessagingTemplate;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.stereotype.Controller;
    import java.time.LocalDateTime;

    @Controller
    public class ChatWebSocketRestController {
        private final SimpMessagingTemplate messagingTemplate;

        public ChatWebSocketRestController(SimpMessagingTemplate messagingTemplate) {
            this.messagingTemplate = messagingTemplate;
        }

        @MessageMapping("/chat.sendToAdmin")
        public void sendMessage(ChatMessage message) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication==null){
               return;
            }
            UserDetails userDetails =(UserDetails) authentication.getPrincipal();
            message.setSender(userDetails.getUsername()); // người gửi
            message.setTimestamp(LocalDateTime.now().toString());
            messagingTemplate.convertAndSendToUser("admin","/queue/message",message);// gửi tin nhắn cho admin
        }

        @MessageMapping("/chat.replyToUser")
        public void repToUser(ChatMessage message){
            message.setSender("admin");
            message.setTimestamp(LocalDateTime.now().toString());
            messagingTemplate.convertAndSendToUser(message.getRecipient(),"/queue/message",message); // gửi cho người nhận
        }

    }
