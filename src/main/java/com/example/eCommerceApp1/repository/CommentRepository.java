package com.example.eCommerceApp1.repository;

import com.example.eCommerceApp1.enitty.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    @Query("select c from CommentEntity c where c.productTemplateId = :productTemplateId order by c.createdAt desc ")
    Page<CommentEntity> findAllByProductTemplateId(Long productTemplateId, Pageable pageable);

    void deleteAllByProductTemplateId(Long productTemplateId);

    List<CommentEntity> findAllByProductTemplateIdIn(List<Long> productTemplateIds);
}
