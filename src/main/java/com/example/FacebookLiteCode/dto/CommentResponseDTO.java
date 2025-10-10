package com.example.FacebookLiteCode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentResponseDTO {
    private int commentId;
    private String content;
    private int postId;
    private int userId;
    private String username;
    private String userProfilePicture;
}
