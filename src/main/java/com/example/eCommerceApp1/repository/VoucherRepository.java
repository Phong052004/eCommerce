package com.example.eCommerceApp1.repository;

import com.example.eCommerceApp1.enitty.VoucherEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface VoucherRepository extends JpaRepository<VoucherEntity, Long> {
    List<VoucherEntity> findAllByShopId(Long shopId);

    Page<VoucherEntity> findAllByIdIn(List<Long> voucherIds, Pageable pageable);

    void deleteByProductTemplateId(Long productTemplateId);

    VoucherEntity searchByCode(String code);

    @Query("SELECT v from VoucherEntity v where v.endDate < :now")
    List<VoucherEntity> searchExpiredVouchers(@Param("now") LocalDateTime now);

    void deleteAllByIdIn(List<Long> voucherIds);

    List<VoucherEntity> findAllByProductTemplateIdIn(List<Long> productTemplateIds);

    VoucherEntity findByProductTemplateId(Long productTemplateId);

    List<VoucherEntity> findAllByProductTemplateIdIn(Set<Long> productTemplateIds);
}
