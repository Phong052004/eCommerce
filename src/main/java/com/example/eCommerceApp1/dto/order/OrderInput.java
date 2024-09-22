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
public class OrderInput {
    private String name;
    private String address;
    private Long phoneNumber;
    private String paymentMethod;
    private List<ProductsInput> orderProducts;
}
