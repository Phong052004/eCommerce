package com.example.eCommerceApp1.service;

import com.example.eCommerceApp1.enitty.ChatEntity;
import com.example.eCommerceApp1.enitty.MessageEntity;
import com.example.eCommerceApp1.enitty.UserEntity;
import com.example.eCommerceApp1.repository.ChatRepository;
import com.example.eCommerceApp1.repository.CustomRepository;
import com.example.eCommerceApp1.repository.MessageRepository;
import com.example.eCommerceApp1.token.TokenHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class WebSocketService extends TextWebSocketHandler {
    public static final Map<Long, WebSocketSession> webSocketSessions = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private final ChatRepository chatRepository;
    @Autowired
    private final CustomRepository customRepository;
    @Autowired
    private final MessageRepository messageRepository;

    public WebSocketService(ChatRepository chatRepository, CustomRepository customRepository, MessageRepository messageRepository) {
        this.chatRepository = chatRepository;
        this.customRepository = customRepository;
        this.messageRepository = messageRepository;
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        String query = session.getUri().getQuery();
        System.out.println(query);
        String accessToken = null;
        if (query != null && query.contains("accessToken")) {
            String[] tokens = query.split("=");
            accessToken = tokens[1];
        }

        accessToken = "Bearer " + accessToken;
        // Verify the accessToken (you would add your own logic to check if it's valid)
        if (accessToken != null) {
            Long userId = TokenHelper.getUserIdFromToken(accessToken);
            session.getAttributes().put("userId", userId); // Store userId in session attributes
            webSocketSessions.put(userId, session);  // Store session by userId
            System.out.println("Connection established for user: " + userId);
        } else {
            System.out.println("Invalid or missing accessToken.");
            session.close(CloseStatus.BAD_DATA);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        super.handleMessage(session, message);

        String payload = message.getPayload().toString();
        Map<String, Object> payloadMap = objectMapper.readValue(payload, Map.class);

        if (payloadMap.get("chatId") != "") {
            Long chatId = objectMapper.convertValue(payloadMap.get("chatId"), Long.class);
            ChatEntity chatEntity = customRepository.getChatEntityBy(chatId);
            chatEntity.setNewestMessage(objectMapper.convertValue(payloadMap.get("message"), String.class));
            chatEntity.setNewestUserId((Long) session.getAttributes().get("userId"));
            chatEntity.setNewestChatTime(LocalDateTime.now());

            Long userId1 = chatEntity.getUserId1();
            Long userId2 = chatEntity.getUserId2();

            UserEntity userEntity1 = customRepository.getUserBy(userId1);
            chatEntity.setImageUrl(userEntity1.getImage());

            UserEntity userEntity2 = customRepository.getUserBy(userId2);
            ChatEntity chatEntity2 = chatRepository.findByUserId1AndUserId2(userId2, userId1);
            chatEntity2.setNewestMessage(objectMapper.convertValue(payloadMap.get("message"), String.class));
            chatEntity2.setNewestUserId((Long) session.getAttributes().get("userId"));
            chatEntity2.setNewestChatTime(LocalDateTime.now());
            chatEntity2.setImageUrl(userEntity2.getImage());

            chatRepository.save(chatEntity2);
            chatRepository.save(chatEntity);

            MessageEntity messageEntity = MessageEntity.builder()
                    .chatId1(chatEntity.getId())
                    .chatId2(chatEntity2.getId())
                    .message(chatEntity.getNewestMessage())
                    .senderId(chatEntity.getUserId1())
                    .createAt(LocalDateTime.now())
                    .build();
            messageRepository.save(messageEntity);
        } else {
            ChatEntity chatEntity1 = ChatEntity.builder()
                    .userId1((Long) session.getAttributes().get("userId"))
                    .userId2(objectMapper.convertValue(payloadMap.get("receivedId"), Long.class))
                    .newestMessage(objectMapper.convertValue(payloadMap.get("message"), String.class))
                    .newestUserId((Long) session.getAttributes().get("userId"))
                    .newestChatTime(LocalDateTime.now())
                    .build();
            ChatEntity chatEntity2 = ChatEntity.builder()
                    .userId1(objectMapper.convertValue(payloadMap.get("receivedId"), Long.class))
                    .userId2((Long) session.getAttributes().get("userId"))
                    .newestMessage(objectMapper.convertValue(payloadMap.get("message"), String.class))
                    .newestUserId((Long) session.getAttributes().get("userId"))
                    .newestChatTime(LocalDateTime.now())
                    .build();

            chatRepository.save(chatEntity1);
            chatRepository.save(chatEntity2);

            MessageEntity messageEntity = MessageEntity.builder()
                    .chatId1(chatEntity1.getId())
                    .chatId2(chatEntity2.getId())
                    .message(chatEntity1.getNewestMessage())
                    .senderId(chatEntity1.getUserId1())
                    .createAt(LocalDateTime.now())
                    .build();
            messageRepository.save(messageEntity);
        }

        Long userId1 = ((Long) session.getAttributes().get("userId"));
        Long userId2 = objectMapper.convertValue(payloadMap.get("receivedId"), Long.class);
        String messages = objectMapper.convertValue(payloadMap.get("message"), String.class);

        // Find the session of the receiver (userId2)
        WebSocketSession receiverSession = webSocketSessions.get(userId2);
        if (receiverSession != null) {
            // Send the message
            TextMessage textMessage = new TextMessage("Message from " + userId1 + ": " + messages);
            receiverSession.sendMessage(textMessage);
            System.out.println("Message sent from " + userId1 + " to " + userId2);
        } else {
            System.out.println("User " + userId2 + " is not connected.");
        }
    }
}
