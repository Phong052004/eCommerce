package com.example.eCommerceApp1.repository.product;

import com.example.eCommerceApp1.enitty.product.TemplateAttributeMapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateAttributeMapRepository extends JpaRepository<TemplateAttributeMapEntity, Long> {
    void deleteAllByProductTemplateId(Long productTemplateId);

    List<TemplateAttributeMapEntity> findAllByProductTemplateId(Long productTemplateId);

    void deleteAllByAttributeIdIn(List<Long> attributeIds);

    void deleteAllByAttributeValueIdIn(List<Long> attributeValueIds);
}
