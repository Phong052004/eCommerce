package com.example.eCommerceApp1.mapper;

import com.example.eCommerceApp1.dto.comment.CommentInput;
import com.example.eCommerceApp1.enitty.CommentEntity;
import org.mapstruct.Mapper;

@Mapper
public interface CommentMapper {
    CommentEntity getEntityFromInput(CommentInput commentInput);
}
