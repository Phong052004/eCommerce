package com.example.eCommerceApp1.dto.cart;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ProductCartMapOutput {
    private Long id;
    private Long shopId;
    private String nameShop;
    private List<ProductsOfShopOutput> productsOutput;
    private Integer totalPrice;
}
