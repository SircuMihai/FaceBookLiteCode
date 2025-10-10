package com.example.FacebookLiteCode.dto.mapper;

import com.example.FacebookLiteCode.dto.CommentRequestDTO;
import com.example.FacebookLiteCode.dto.CommentResponseDTO;
import com.example.FacebookLiteCode.model.Coments;
import com.example.FacebookLiteCode.model.Post;
import com.example.FacebookLiteCode.model.Users;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    
    // Convert Entity to Response DTO
    public CommentResponseDTO toResponseDTO(Coments comment) {
        if (comment == null) {
            return null;
        }
        
        CommentResponseDTO dto = new CommentResponseDTO();
        dto.setCommentId(comment.getCommentId());
        dto.setContent(comment.getContent());
        
        if (comment.getPost() != null) {
            dto.setPostId(comment.getPost().getPostId());
        }
        
        if (comment.getUser() != null) {
            dto.setUserId(comment.getUser().getUserId());
            dto.setUsername(comment.getUser().getUsername());
            dto.setUserProfilePicture(comment.getUser().getProfilePicture());
        }
        
        return dto;
    }
    
    // Convert Request DTO to Entity
    public Coments toEntity(CommentRequestDTO dto, Users user, Post post) {
        if (dto == null) {
            return null;
        }
        
        Coments comment = new Coments();
        comment.setContent(dto.getContent());
        comment.setUser(user);
        comment.setPost(post);
        
        return comment;
    }
    
    // Update existing entity from Request DTO
    public void updateEntityFromDTO(CommentRequestDTO dto, Coments comment) {
        if (dto == null || comment == null) {
            return;
        }
        
        if (dto.getContent() != null) {
            comment.setContent(dto.getContent());
        }
    }
}
