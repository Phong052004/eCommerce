package com.example.eCommerceApp1.enitty;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "tbl_message")
@Builder
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "chatId_1")
    private Long chatId1;
    @Column(name = "chatId_2")
    private Long chatId2;
    private String message;
    private Long senderId;
    private LocalDateTime createAt;
}
