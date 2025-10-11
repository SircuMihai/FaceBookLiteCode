package com.example.FacebookLiteCode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonAlias;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentRequestDTO {
    @NotBlank(message = "Content is required")
    @Size(max = 1000, message = "Comment must not exceed 1000 characters")
    private String content;
    
    @NotNull(message = "Post ID is required")
    @JsonAlias({"post_id", "postId"})
    private Integer postId;

    @NotNull(message = "User ID is required")
    @JsonAlias({"user_id", "userId"})
    private Integer userId;
}
