package com.salesapp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                try {
                    Jwt jwt = jwtDecoder.decode(token);
                    String userId = jwt.getSubject(); // hoặc jwt.getClaim("sub"), hoặc "username"
                    accessor.setUser(new Principal() {
                        @Override
                        public String getName() {
                            return userId;
                        }
                    });
                } catch (Exception ex) {
                    throw new RuntimeException("JWT token không hợp lệ hoặc đã hết hạn", ex);
                }
            } else {
                throw new RuntimeException("Không tìm thấy JWT token trong header CONNECT");
            }
        }

        return message;
    }
}
