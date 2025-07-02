package com.salesapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // Cấu hình endpoint mà client sẽ connect
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*")
                .withSockJS(); // Hỗ trợ fallback khi không có WebSocket
    }

    // Cấu hình broker để định tuyến tin nhắn
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue"); // nơi để gửi message đến client
        registry.setApplicationDestinationPrefixes("/app"); // prefix cho tin nhắn từ client gửi lên server
        registry.setUserDestinationPrefix("/user"); // gửi riêng cho user
    }
}
