package com.example.eCommerceApp1.dto.order;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ProductOrderInput {
    private Long shoppingCartId;
    private Long productId;
    private Long productTemplateId;
    private String name;
    private Integer quantityOrder;
    private Integer price;
    private String image;
}
