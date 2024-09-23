package com.example.eCommerceApp1.service;

import com.example.eCommerceApp1.common.Common;
import com.example.eCommerceApp1.dto.comment.CommentInput;
import com.example.eCommerceApp1.dto.comment.CommentOutput;
import com.example.eCommerceApp1.enitty.CommentEntity;
import com.example.eCommerceApp1.enitty.UserEntity;
import com.example.eCommerceApp1.helper.StringUtils;
import com.example.eCommerceApp1.mapper.CommentMapper;
import com.example.eCommerceApp1.repository.CommentRepository;
import com.example.eCommerceApp1.repository.CustomRepository;
import com.example.eCommerceApp1.repository.UserRepository;
import com.example.eCommerceApp1.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final CustomRepository customRepository;
    private final UserRepository userRepository;

    @Transactional
    public void commentProduct(String accessToken, CommentInput commentInput, Long productTemplateId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        CommentEntity commentEntity = commentMapper.getEntityFromInput(commentInput);
        commentEntity.setUserId(userId);
        commentEntity.setProductTemplateId(productTemplateId);
        commentEntity.setImages(StringUtils.getStringFromList(commentInput.getImageUrls()));
        commentEntity.setCreatedAt(LocalDateTime.now());
        commentRepository.save(commentEntity);
    }

    @Transactional
    public void deleteComment(String accessToken, Long commentId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        CommentEntity commentEntity = customRepository.getCommentEntityBy(commentId);
        if (!commentEntity.getUserId().equals(userId)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        commentRepository.deleteById(commentId);
    }

    @Transactional
    public void changeComment(String accessToken, CommentInput commentInput, Long commentId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        CommentEntity commentEntity = customRepository.getCommentEntityBy(commentId);
        if (!commentEntity.getUserId().equals(userId)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        commentEntity.setComment(commentInput.getComment());
        commentEntity.setCreatedAt(LocalDateTime.now());
        commentEntity.setRating(commentInput.getRating());
        commentEntity.setImages(StringUtils.getStringFromList(commentInput.getImageUrls()));
        commentRepository.save(commentEntity);
    }

    @Transactional(readOnly = true)
    public Page<CommentOutput> getComments(Long productTemplateId, Pageable pageable) {
        Page<CommentEntity> commentEntityPage = commentRepository.findAllByProductTemplateId(productTemplateId, pageable);
        if (Objects.isNull(commentEntityPage) || commentEntityPage.isEmpty()) {
            return Page.empty();
        }

        Map<Long, UserEntity> userEntityMap = userRepository.findAllByIdIn(
                commentEntityPage.stream().map(CommentEntity::getUserId).collect(Collectors.toSet())
        ).stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        return commentEntityPage.map(
                commentEntity -> {
                    UserEntity userEntity = userEntityMap.get(commentEntity.getUserId());
                    CommentOutput commentOutput = CommentOutput.builder()
                            .userId(userEntity.getId())
                            .fullName(userEntity.getFullName())
                            .avatarImage(userEntity.getImage())
                            .comment(commentEntity.getComment())
                            .rating(commentEntity.getRating())
                            .imageUrls(StringUtils.getListFromString(commentEntity.getImages()))
                            .build();
                    return commentOutput;
                }
        );
    }
}
