package com.example.eCommerceApp1.repository;

import com.example.eCommerceApp1.enitty.ProductCartMapEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductCartMapRepository extends JpaRepository<ProductCartMapEntity, Long> {
    ProductCartMapEntity findByProductIdAndUserId(Long productId, Long userId);

    Page<ProductCartMapEntity> findAllByUserId(Long userId, Pageable pageable);

    List<ProductCartMapEntity> findAllByIdIn(List<Long> cartIds);

    void deleteAllByIdIn(List<Long> cartIds);

    void deleteAllByProductIdIn(Set<Long> productId);

    List<ProductCartMapEntity> findAllByProductIdIn(Set<Long> productId);
}
