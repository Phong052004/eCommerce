package com.example.eCommerceApp1.dto.voucher;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class VoucherInput {
    private String name;
    private Double saleOff;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
