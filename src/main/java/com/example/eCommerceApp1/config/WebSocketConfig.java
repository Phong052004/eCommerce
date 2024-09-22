package com.example.eCommerceApp1.config;

import com.example.eCommerceApp1.repository.ChatRepository;
import com.example.eCommerceApp1.repository.CustomRepository;
import com.example.eCommerceApp1.repository.MessageRepository;
import com.example.eCommerceApp1.service.WebSocketService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@AllArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final ChatRepository chatRepository;
    private final CustomRepository customRepository;
    private final MessageRepository messageRepository;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(new WebSocketService(
                chatRepository, customRepository, messageRepository
        ), "/chat").setAllowedOrigins("*");
    }
}
