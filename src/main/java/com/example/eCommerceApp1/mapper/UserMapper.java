package com.example.eCommerceApp1.mapper;

import com.example.eCommerceApp1.dto.user.ChangeInfoUserRequest;
import com.example.eCommerceApp1.dto.user.UserRequest;
import com.example.eCommerceApp1.enitty.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface UserMapper {
    UserEntity getEntityFromInput(UserRequest userRequest);

    void updateEntityFromInput(@MappingTarget UserEntity userEntity, ChangeInfoUserRequest changeInfoUserRequest);
}