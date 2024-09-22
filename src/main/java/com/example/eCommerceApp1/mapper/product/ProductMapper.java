package com.example.eCommerceApp1.mapper.product;

import com.example.eCommerceApp1.dto.product.ProductInput;
import com.example.eCommerceApp1.enitty.product.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface ProductMapper {
    ProductEntity getEntityFromInput(ProductInput productInput);

    void updateEntityFormInput(@MappingTarget ProductEntity productEntity, ProductInput productInput);
}
