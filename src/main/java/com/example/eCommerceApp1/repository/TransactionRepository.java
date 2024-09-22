package com.example.eCommerceApp1.repository;

import com.example.eCommerceApp1.enitty.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    boolean existsByTransNo(String transNo);
}
