package com.example.eCommerceApp1.service;

import com.example.eCommerceApp1.common.Common;
import com.example.eCommerceApp1.dto.user.ChangeInfoUserRequest;
import com.example.eCommerceApp1.dto.user.UserRequest;
import com.example.eCommerceApp1.enitty.UserEntity;
import com.example.eCommerceApp1.mapper.UserMapper;
import com.example.eCommerceApp1.repository.CustomRepository;
import com.example.eCommerceApp1.repository.UserRepository;
import com.example.eCommerceApp1.token.TokenHelper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CustomRepository customRepository;

    @Transactional
    public String signUp(UserRequest signUpRequest) {
        if(Boolean.TRUE.equals(userRepository.existsByUsername(signUpRequest.getUsername()))) {
            throw new RuntimeException(Common.USERNAME_IS_EXISTS);
        }
        signUpRequest.setPassword(BCrypt.hashpw(signUpRequest.getPassword(), BCrypt.gensalt()));
        UserEntity userEntity = userMapper.getEntityFromInput(signUpRequest);
        UUID uuid = UUID.randomUUID();
        userEntity.setFullName("USER" + uuid);
        userEntity.setIsShop(Boolean.FALSE);
        userEntity.setAverageRating(0.0);
        userEntity.setFollowing(0);
        userEntity.setFollowers(0);
        userEntity.setTotalComment(0);
        userRepository.save(userEntity);
        return TokenHelper.generateToken(userEntity);
    }

    @Transactional
    public String logIn(UserRequest logInRequest) {
        UserEntity userEntity = userRepository.findByUsername(logInRequest.getUsername());
        if(Objects.isNull(userEntity)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        String currentHashedPassword = userEntity.getPassword();
        if(BCrypt.checkpw(logInRequest.getPassword(),currentHashedPassword)) {
            return TokenHelper.generateToken(userEntity);
        }
        throw new RuntimeException(Common.INCORRECT_PASSWORD);
    }

    @Transactional
    public void changeInformation(ChangeInfoUserRequest changeInfoUserRequest, String accessToken) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity userEntity = customRepository.getUserBy(userId);
        userMapper.updateEntityFromInput(userEntity,changeInfoUserRequest);
        userRepository.save(userEntity);
    }

    @Transactional
    public void registerShop(String accessToken) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity userEntity = customRepository.getUserBy(userId);
        userEntity.setTotalProduct(0);
        userEntity.setTotalComment(0);
        userEntity.setIsShop(Boolean.TRUE);
        userRepository.save(userEntity);
    }
}

