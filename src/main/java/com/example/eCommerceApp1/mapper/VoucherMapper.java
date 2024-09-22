package com.example.eCommerceApp1.mapper;

import com.example.eCommerceApp1.dto.voucher.VoucherInput;
import com.example.eCommerceApp1.enitty.VoucherEntity;
import org.mapstruct.Mapper;

@Mapper
public interface VoucherMapper {
    VoucherEntity getEntityFromInput(VoucherInput voucherInput);
}
