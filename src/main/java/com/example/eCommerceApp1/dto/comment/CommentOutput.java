package com.example.eCommerceApp1.dto.comment;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class CommentOutput {
    private Long id;
    private Long userId;
    private String fullName;
    private String avatarImage;
    private String comment;
    private Integer rating;
    private List<String> imageUrls;
}
