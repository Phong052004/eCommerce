package com.example.eCommerceApp1.dto.cart;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ProductsOfShopOutput {
    private Long productTemplateId;
    private Long productId;
    private String productName;
    private Integer price;
    private Integer quantityOrder;
    private String imageUrl;
    private Integer totalPrice;
}
