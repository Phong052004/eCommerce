package com.example.eCommerceApp1.mapper.product;

import com.example.eCommerceApp1.dto.product.ProductTemplateInput;
import com.example.eCommerceApp1.enitty.product.ProductTemplateEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface ProductTemplateMapper {
    ProductTemplateEntity getEntityFromInput(ProductTemplateInput productTemplateInput);

    void updateEntityFromInput(@MappingTarget ProductTemplateEntity productTemplateEntity,
                               ProductTemplateInput productTemplateInput);
}
