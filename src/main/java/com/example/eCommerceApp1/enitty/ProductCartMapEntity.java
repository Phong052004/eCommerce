package com.example.eCommerceApp1.enitty;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "tbl_product_cart_map")
@Builder
public class ProductCartMapEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long productId;
    private Long shopId;
    private Long productTemplateId;
    private Integer quantityOrder;
    private LocalDateTime createdAt;
}
