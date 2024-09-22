package com.example.eCommerceApp1.repository;

import com.example.eCommerceApp1.enitty.UserVoucherMapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping
public interface UserVoucherMapRepository extends JpaRepository<UserVoucherMapEntity, Long> {
    List<UserVoucherMapEntity> findAllByUserId(Long userId);

    boolean existsByVoucherId(Long voucherId);

    void deleteAllByVoucherId(Long voucherId);

    void deleteAllByVoucherIdIn(List<Long> voucherIds);
}
