package com.example.eCommerceApp1.mapper;

import com.example.eCommerceApp1.dto.order.OrderOutput;
import com.example.eCommerceApp1.enitty.UserOrderEntity;
import org.mapstruct.Mapper;

@Mapper
public interface UserOrderMapper {
    OrderOutput getOutputFromEntity(UserOrderEntity userOrderEntity);
}
