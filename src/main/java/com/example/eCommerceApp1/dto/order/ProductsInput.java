package com.example.eCommerceApp1.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProductsInput {
    private Long shopId;
    private String nameShop;
    private List<ProductOrderInput> productOrderInputs;
    private Integer totalPrice;
}
