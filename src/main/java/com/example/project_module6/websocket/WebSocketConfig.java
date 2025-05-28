package com.example.project_module6.websocket;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocketMessageBroker // kích hoạt websocket
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Autowired
    private JwtWebSocketInterceptor jwtWebSocketInterceptor;
    @Autowired
    private WebSocketChannelInterceptor webSocketChannelInterceptor;
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/queue"); // client sẽ nhận tin nhắn ở đây do server gửi về
        config.setApplicationDestinationPrefixes("/app"); // client sẽ gửi tin nhắn ở đây
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat") // kết nối với websocket tại đây
                .setAllowedOrigins("http://localhost:3000")
                .addInterceptors(jwtWebSocketInterceptor)
                .withSockJS(); // hổ trợ fallback nếu trình duyệt không hổ trợ websocket
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketChannelInterceptor);
    }
}
