package com.example.eCommerceApp1.enitty;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "tbl_product_order_map")
@Builder
public class ProductOrderMapEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private Long shopId;
    private Long productId;
    private Long productTemplateId;
    private String nameProduct;
    private String imageProduct;
    private Integer quantityOrder;
    private Integer price;
}
