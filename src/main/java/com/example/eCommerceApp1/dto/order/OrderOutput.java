package com.example.eCommerceApp1.dto.order;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class OrderOutput {
    private Long shopId;
    private String nameShop;
    private List<ProductOrderInput> productOrderOutputs;
    private String state;
    private Integer totalPrice;
}
