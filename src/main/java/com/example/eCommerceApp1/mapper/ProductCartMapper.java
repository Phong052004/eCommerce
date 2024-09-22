package com.example.eCommerceApp1.mapper;

import com.example.eCommerceApp1.dto.cart.ProductCartInput;
import com.example.eCommerceApp1.enitty.ProductCartMapEntity;
import org.mapstruct.Mapper;

@Mapper
public interface ProductCartMapper {
    ProductCartMapEntity getEntityFromInput(ProductCartInput productCartInput);
}
