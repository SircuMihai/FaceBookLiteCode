package com.example.FacebookLiteCode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDTO {
    @NotBlank(message = "Comment content is required")
    @Size(max = 50, message = "Comment must not exceed 50 words")
    private String content;
    
    @NotNull(message = "Post ID is required")
    private Integer postId;
    
    @NotNull(message = "User ID is required")
    private Integer userId;
}