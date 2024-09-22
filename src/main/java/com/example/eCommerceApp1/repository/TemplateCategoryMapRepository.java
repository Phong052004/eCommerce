package com.example.eCommerceApp1.repository;

import com.example.eCommerceApp1.enitty.TemplateCategoryMapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateCategoryMapRepository extends JpaRepository<TemplateCategoryMapEntity, Long> {
    TemplateCategoryMapEntity findByCategoryIdAndTemplateProductId(Long categoryId, Long templateId);

    List<TemplateCategoryMapEntity> findAllByCategoryId(Long categoryId);
}
