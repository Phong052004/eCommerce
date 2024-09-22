package com.example.eCommerceApp1.service;

import com.example.eCommerceApp1.dto.chat.ChatOutput;
import com.example.eCommerceApp1.dto.message.MessageOutput;
import com.example.eCommerceApp1.enitty.ChatEntity;
import com.example.eCommerceApp1.enitty.MessageEntity;
import com.example.eCommerceApp1.enitty.UserEntity;
import com.example.eCommerceApp1.repository.ChatRepository;
import com.example.eCommerceApp1.repository.CustomRepository;
import com.example.eCommerceApp1.repository.MessageRepository;
import com.example.eCommerceApp1.repository.UserRepository;
import com.example.eCommerceApp1.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final CustomRepository customRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public Page<ChatOutput> getChatList(String accessToken, Pageable pageable) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);

        Page<ChatEntity> chatEntityPage = chatRepository.findAllByUserId1(userId, pageable);
        if (Objects.isNull(chatEntityPage) || chatEntityPage.isEmpty()) {
            return Page.empty();
        }

        Map<Long, UserEntity> userEntityMap = userRepository.findAllByIdIn(
                chatEntityPage.stream().map(ChatEntity::getUserId2).collect(Collectors.toSet())
        ).stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        return chatEntityPage.map(
                chatEntity -> {
                    UserEntity userEntity = userEntityMap.get(chatEntity.getUserId2());
                    ChatOutput chatOutput = ChatOutput.builder()
                            .name(userEntity.getFullName())
                            .imageUrl(userEntity.getImage())
                            .newestMessage(chatEntity.getNewestMessage())
                            .isMe(Boolean.FALSE)
                            .newestChatTime(chatEntity.getNewestChatTime())
                            .build();
                    return chatOutput;
                }
        );
    }

    public Page<MessageOutput> getMessages(String accessToken,Long chatId, Pageable pageable) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        ChatEntity chatEntity = customRepository.getChatEntityBy(chatId);
        Page<MessageEntity> messageEntityPage = messageRepository.searchAllByChatId(chatId, pageable);
        if (Objects.isNull(messageEntityPage) || messageEntityPage.isEmpty()) {
            return Page.empty();
        }

        List<Long> userIds = new ArrayList<>();
        userIds.add(chatEntity.getUserId2());
        userIds.add(chatEntity.getUserId1());
        Map<Long, UserEntity> userEntityMap = userRepository.findAllByIdIn(userIds)
                .stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        return messageEntityPage.map(
                messageEntity -> {
                    UserEntity userEntity = userEntityMap.get(messageEntity.getSenderId());
                    MessageOutput messageOutput = new MessageOutput();
                    messageOutput.setUserId(userEntity.getId());
                    messageOutput.setMessage(messageEntity.getMessage());
                    messageOutput.setFullName(userEntity.getFullName());
                    messageOutput.setImageUrl(userEntity.getImage());
                    messageOutput.setCreatedAt(messageEntity.getCreateAt());
                    messageOutput.setIsMe(
                            userId.equals(messageEntity.getSenderId()) ? Boolean.TRUE : Boolean.FALSE
                    );
                    return messageOutput;
                }
        );
    }
}
