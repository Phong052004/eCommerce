package com.example.eCommerceApp1.repository.product;

import com.example.eCommerceApp1.enitty.product.ProductTemplateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductTemplateRepository extends JpaRepository<ProductTemplateEntity, Long> {
    Page<ProductTemplateEntity> findAllByShopId(Long shopId, Pageable pageable);

    Page<ProductTemplateEntity> findAllByIdIn(List<Long> ids, Pageable pageable);

    @Query("SELECT u FROM ProductTemplateEntity u WHERE u.name LIKE %?1%")
    Page<ProductTemplateEntity> searchProductTemplateEntitiesByString(String search, Pageable pageable);

    List<ProductTemplateEntity> findAllByIdIn(Set<Long> productTemplateIds);
}
