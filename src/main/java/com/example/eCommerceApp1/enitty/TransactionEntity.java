package com.example.eCommerceApp1.enitty;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "tbl_transaction")
@Builder
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long orderId;
    private String billNo;//vnp_TxnRef
    private String transNo;//vnp_TransactionNo
    private String bankCode;// vnp_BankCode
    private String cardType;// vnp_CardType
    private Integer amount;
    private String currency;
    private String bankAccountNo;
    private String bankAccount;
    private String refundBankCode;
    private String reason;
    private LocalDateTime createDate;
}
