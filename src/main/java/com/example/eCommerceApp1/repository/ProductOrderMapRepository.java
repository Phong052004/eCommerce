package com.example.eCommerceApp1.repository;

import com.example.eCommerceApp1.enitty.ProductOrderMapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOrderMapRepository extends JpaRepository<ProductOrderMapEntity, Long> {
    List<ProductOrderMapEntity> findAllByOrderIdIn(List<Long> orderIds);

    List<ProductOrderMapEntity> findAllByOrderId(Long orderId);
}
