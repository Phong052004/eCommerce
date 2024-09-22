package com.example.eCommerceApp1.enitty.product;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "tbl_template_attribute")
public class TemplateAttributeMapEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productTemplateId;
    private Long attributeId;
    private Long attributeValueId;
}
