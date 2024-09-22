package com.example.eCommerceApp1.repository;

import com.example.eCommerceApp1.enitty.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {
    UserEntity findByUsername(String username);

    Boolean existsByUsername(String username);

    List<UserEntity> findAllByIdIn(Set<Long> shopIds);

    List<UserEntity> findAllByIdIn(List<Long> shopIds);

}
