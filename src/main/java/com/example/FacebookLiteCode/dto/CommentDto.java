package com.example.FacebookLiteCode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long commentId;
    private String content;
    private Long postId;
    private Long userId;
    private String username;
    private String createdAt;
}
