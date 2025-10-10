package com.example.FacebookLiteCode.dto.mapper;

import com.example.FacebookLiteCode.dto.PostRequestDTO;
import com.example.FacebookLiteCode.dto.PostResponseDTO;
import com.example.FacebookLiteCode.model.Post;
import com.example.FacebookLiteCode.model.Users;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class PostMapper {
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // Convert Entity to Response DTO
    public PostResponseDTO toResponseDTO(Post post) {
        if (post == null) {
            return null;
        }
        
        PostResponseDTO dto = new PostResponseDTO();
        dto.setPostId(post.getPostId());
        dto.setContent(post.getContent());
        dto.setCreatedAt(post.getCreatedAt());
        
        if (post.getUser() != null) {
            dto.setUserId(post.getUser().getUserId());
            dto.setUsername(post.getUser().getUsername());
            dto.setUserProfilePicture(post.getUser().getProfilePicture());
        }
        
        return dto;
    }
    
    // Convert Request DTO to Entity
    public Post toEntity(PostRequestDTO dto, Users user) {
        if (dto == null) {
            return null;
        }
        
        Post post = new Post();
        post.setContent(dto.getContent());
        post.setCreatedAt(LocalDateTime.now().format(formatter));
        post.setUser(user);
        
        return post;
    }
    
    // Update existing entity from Request DTO
    public void updateEntityFromDTO(PostRequestDTO dto, Post post) {
        if (dto == null || post == null) {
            return;
        }
        
        if (dto.getContent() != null) {
            post.setContent(dto.getContent());
        }
    }
}
