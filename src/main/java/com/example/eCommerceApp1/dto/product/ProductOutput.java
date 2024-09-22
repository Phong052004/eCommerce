package com.example.eCommerceApp1.dto.product;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ProductOutput {
    private Long id;
    private Long productId;
    private String name;
    private Integer quantity;
    private Integer price;
    private String imageUrl;
    private Integer discountedPrice;
    private Double saleOff;
    private Boolean existed;
}
