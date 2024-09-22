package com.example.eCommerceApp1.dto.voucher;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class VoucherOutput {
    private Long id;
    private Long shopId;
    private Long productTemplateId;
    private String nameShop;
    private String imageShop;
    private String name;
    private String code;
    private Double saleOff;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
