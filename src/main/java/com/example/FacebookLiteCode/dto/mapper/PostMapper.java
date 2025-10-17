package com.example.FacebookLiteCode.dto.mapper;

import com.example.FacebookLiteCode.dto.PostRequestDTO;
import com.example.FacebookLiteCode.dto.PostResponseDTO;
import com.example.FacebookLiteCode.dto.CommentResponseDTO;
import com.example.FacebookLiteCode.model.Post;
import com.example.FacebookLiteCode.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostMapper {
    
    @Autowired
    private CommentMapper commentMapper;
    
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
        dto.setLikesCount(post.getLikesCount() != null ? post.getLikesCount() : 0);
        
        if (post.getUser() != null) {
            dto.setUserId(post.getUser().getUserId());
            dto.setUsername(post.getUser().getUsername());
            dto.setUserProfilePicture(post.getUser().getProfilePicture());
        }
        
        // Load comments if available
        try {
            if (post.getComments() != null && !post.getComments().isEmpty()) {
                List<CommentResponseDTO> commentDTOs = post.getComments().stream()
                        .map(commentMapper::toResponseDTO)
                        .collect(Collectors.toList());
                dto.setComments(commentDTOs);
            } else {
                dto.setComments(new java.util.ArrayList<>());
            }
        } catch (Exception e) {
            // If there's an issue loading comments, set empty list
            System.err.println("Error loading comments: " + e.getMessage());
            dto.setComments(new java.util.ArrayList<>());
        }
        
        return dto;
    }
    
    // Convert Entity to Response DTO with comments
    public PostResponseDTO toResponseDTOWithComments(Post post) {
        PostResponseDTO dto = toResponseDTO(post);
        
        // Load comments if available - temporarily commented out
        /*
        try {
            if (post.getComments() != null && !post.getComments().isEmpty()) {
                List<CommentResponseDTO> commentDTOs = post.getComments().stream()
                        .map(commentMapper::toResponseDTO)
                        .collect(Collectors.toList());
                dto.setComments(commentDTOs);
            }
        } catch (Exception e) {
            // If there's an issue loading comments, keep empty list
            System.err.println("Error loading comments: " + e.getMessage());
        }
        */
        
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
