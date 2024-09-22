package com.example.eCommerceApp1.dto.user;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class UserOutput {
    private Long id;
    private String fullName;
    private String imageUrl;
}