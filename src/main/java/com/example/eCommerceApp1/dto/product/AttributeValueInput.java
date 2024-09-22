package com.example.eCommerceApp1.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AttributeValueInput {
    private Long attributeValueId;
    private String name;
    private Boolean isOfShop;
    private Boolean existed;
}
