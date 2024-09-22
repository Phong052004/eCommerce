package com.example.eCommerceApp1.dto.product;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ProductsTemplateOutput {
    private Long id;
    private Long productTemplateId;
    private String name;
    private Integer minPrice;
    private Integer maxPrice;
    private Integer quantity;
    private String description;
    private String avatarImage;
    private Integer discountedPrice;
    private Double saleOff;
    private Integer soldCount;
    private Double averageRate;
}
