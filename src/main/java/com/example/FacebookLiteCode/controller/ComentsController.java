package com.example.FacebookLiteCode.controller;

import com.example.FacebookLiteCode.services.ComentsService;
import com.example.FacebookLiteCode.repository.UsersRepository;
import com.example.FacebookLiteCode.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.example.FacebookLiteCode.dto.CommentRequestDTO;
import com.example.FacebookLiteCode.dto.CommentResponseDTO;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*")
public class ComentsController {
    
    @Autowired
    private ComentsService comentsService;
    
    @Autowired
    private UsersRepository usersRepository;
    
    @GetMapping
    public List<CommentResponseDTO> getAllComments() {
        return comentsService.getAllCommentsDTO();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseDTO> getCommentById(@PathVariable int id) {
        CommentResponseDTO dto = comentsService.getCommentResponseById(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }
    
    @PostMapping
    public ResponseEntity<CommentResponseDTO> createComment(@Valid @RequestBody CommentRequestDTO request) {
        return ResponseEntity.ok(comentsService.createComment(request));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CommentResponseDTO> updateComment(@PathVariable int id, @Valid @RequestBody CommentRequestDTO request) {
        return comentsService.updateComment(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Delete a comment - Users can only delete their own comments
     * Admins can delete any comment
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteComment(@PathVariable int id) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Authentication required");
            return ResponseEntity.status(401).body(error);
        }
        
        String username = authentication.getName();
        Users currentUser = usersRepository.findByUsername(username)
                .orElse(null);
        
        if (currentUser == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "User not found");
            return ResponseEntity.status(401).body(error);
        }
        
        // Check if comment exists
        CommentResponseDTO comment = comentsService.getCommentResponseById(id);
        if (comment == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Check if user is the owner of the comment OR is an admin
        boolean isOwner = comment.getUserId() == currentUser.getUserId();
        boolean isAdmin = "ADMIN".equals(currentUser.getRole());
        
        if (!isOwner && !isAdmin) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "You can only delete your own comments");
            return ResponseEntity.status(403).body(error);
        }
        
        // Delete the comment
        comentsService.deleteComment(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Comment deleted successfully");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/post/{postId}")
    public List<CommentResponseDTO> getCommentsByPostId(@PathVariable int postId) {
        return comentsService.getCommentsByPostIdDTO(postId);
    }
    
    @GetMapping("/user/{userId}")
    public List<CommentResponseDTO> getCommentsByUserId(@PathVariable int userId) {
        return comentsService.getCommentsByUserIdDTO(userId);
    }
    
    @GetMapping("/search/content/{content}")
    public List<CommentResponseDTO> searchCommentsByContent(@PathVariable String content) {
        return comentsService.getCommentsByContentDTO(content);
    }
}
