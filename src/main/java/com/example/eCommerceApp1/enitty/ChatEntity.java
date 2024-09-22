package com.example.eCommerceApp1.enitty;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "tbl_chat")
@Builder
public class ChatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "userId_1")
    private Long userId1;
    @Column(name = "userId_2")
    private Long userId2;
    private String newestMessage;
    private String imageUrl;
    private LocalDateTime newestChatTime;
    private Long newestUserId;
}
