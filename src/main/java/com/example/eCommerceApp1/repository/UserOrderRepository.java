package com.example.eCommerceApp1.repository;

import com.example.eCommerceApp1.enitty.UserOrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserOrderRepository extends JpaRepository<UserOrderEntity, Long> {
    Page<UserOrderEntity> findAllByUserId(Long userId, Pageable pageable);

    Page<UserOrderEntity> findAllByUserIdAndStateOrder(Long userId, String stateOrder, Pageable pageable);

    List<UserOrderEntity> findAllByIdIn(List<Long> orderIds);

    @Query(value = "SELECT * FROM tbl_user_order uo " +
            "WHERE TIMESTAMPDIFF(DAY, uo.create_at, CURRENT_TIMESTAMP) > 15 AND uo.state_order = :stateOrder",
            nativeQuery = true)
    List<UserOrderEntity> findAllFailedOrder(String stateOrder);

    void deleteAllByIdIn(List<Long> orderIds);
}
