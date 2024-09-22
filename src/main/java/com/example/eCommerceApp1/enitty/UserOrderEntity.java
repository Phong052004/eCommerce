package com.example.eCommerceApp1.enitty;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "tbl_user_order")
@Builder
public class UserOrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String address;
    private Long phoneNumber;
    private String name;
    private String stateOrder;
    private LocalDate createAt;
}
