package com.example.eCommerceApp1.enitty;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "tbl_voucher")
@Builder
public class VoucherEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productTemplateId;
    private Long shopId;
    private String name;
    private String code;
    private Double saleOff;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
