package com.example.FacebookLiteCode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonAlias;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostRequestDTO {
    @NotBlank(message = "Content is required")
    @Size(max = 5000, message = "Content must not exceed 5000 characters")
    private String content;

    @NotNull(message = "user_id is required")
    @JsonAlias({"user_id", "userId"})
    private Integer userId;
}
