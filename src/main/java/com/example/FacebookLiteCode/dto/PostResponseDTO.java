package com.example.FacebookLiteCode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostResponseDTO {
    private int postId;
    private String content;
    private String createdAt;
    private int userId;
    private String username;
    private String userProfilePicture;
    private int likesCount;
    // private java.util.List<CommentResponseDTO> comments; // Temporarily disabled
}
