package com.example.eCommerceApp1.repository.product;

import com.example.eCommerceApp1.enitty.product.AttributeValueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface AttributeValueRepository extends JpaRepository<AttributeValueEntity,Long> {
    List<AttributeValueEntity> findAllByIsOfShop(Boolean False);

    List<AttributeValueEntity> findAllByIdIn(Set<Long> attributeValueIds);

    List<AttributeValueEntity> findAllByAttributeIdAndIsOfShop(Long attributeId, Boolean False);

    List<AttributeValueEntity> findAllByAttributeIdIn(List<Long> attributeIds);

    void deleteAllByIdInAndIsOfShop(Set<Long> attributeValueIds, Boolean True);

    void deleteAllByAttributeIdAndIsOfShop(Long attributeId, Boolean True);

    void deleteAllByIdIn(List<Long> attributeValueIds);
}
