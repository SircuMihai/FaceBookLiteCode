package com.example.FacebookLiteCode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private Long postId;
    private String content;
    private String createdAt;
    private Long userId;
    private String username;
    private String profilePicture;
    private int commentCount;
    private List<CommentDto> comments;
}
