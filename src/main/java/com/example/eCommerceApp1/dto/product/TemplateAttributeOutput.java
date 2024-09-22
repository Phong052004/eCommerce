package com.example.eCommerceApp1.dto.product;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TemplateAttributeOutput {
    private Long id;
    private Long productTemplateId;
    private List<AttributeOutput> attributeOutputs;
}
