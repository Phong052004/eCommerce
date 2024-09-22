package com.example.eCommerceApp1.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProductCartInput {
    private Long shopId;
    private Long productId;
    private Long productTemplateId;
    private Integer quantityOrder;
}
