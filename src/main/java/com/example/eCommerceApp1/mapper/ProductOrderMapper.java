package com.example.eCommerceApp1.mapper;

import com.example.eCommerceApp1.dto.order.ProductOrderInput;
import com.example.eCommerceApp1.enitty.ProductOrderMapEntity;
import org.mapstruct.Mapper;

@Mapper
public interface ProductOrderMapper {
    ProductOrderInput getOutputFromEntity(ProductOrderMapEntity productOrderMapEntity);
}
