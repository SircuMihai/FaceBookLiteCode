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
    
    /**
     * Create comment - Only ADMIN role can create comments
     * Regular users (USER role) are blocked
     */
    @PostMapping
    public ResponseEntity<?> createComment(@Valid @RequestBody CommentRequestDTO request) {
        // Get current authenticated user from JWT token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Authentication required");
            return ResponseEntity.status(401).body(error);
        }
        
        // Extract username from JWT token (set by JwtAuthenticationFilter)
        String username = authentication.getName();
        Users currentUser = usersRepository.findByUsername(username)
                .orElse(null);
        
        if (currentUser == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "User not found");
            return ResponseEntity.status(401).body(error);
        }
        
        // Check role - only ADMIN can create comments
        String role = currentUser.getRole() != null ? currentUser.getRole() : "USER";
        if (!"ADMIN".equals(role)) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Access denied. Only administrators can create comments.");
            return ResponseEntity.status(403).body(error);
        }
        
        // Admin can create comment
        CommentResponseDTO created = comentsService.createComment(request);
        return ResponseEntity.ok(created);
    }
    
    /**
     * Update comment - Only ADMIN role can update comments
     * Regular users (USER role) are blocked
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(@PathVariable int id, @Valid @RequestBody CommentRequestDTO request) {
        // Get current authenticated user from JWT token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Authentication required");
            return ResponseEntity.status(401).body(error);
        }
        
        // Extract username from JWT token (set by JwtAuthenticationFilter)
        String username = authentication.getName();
        Users currentUser = usersRepository.findByUsername(username)
                .orElse(null);
        
        if (currentUser == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "User not found");
            return ResponseEntity.status(401).body(error);
        }
        
        // Check role - only ADMIN can update comments
        String role = currentUser.getRole() != null ? currentUser.getRole() : "USER";
        if (!"ADMIN".equals(role)) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Access denied. Only administrators can update comments.");
            return ResponseEntity.status(403).body(error);
        }
        
        // Admin can update comment
        return comentsService.updateComment(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Delete comment - Only ADMIN role can delete comments
     * Regular users (USER role) are blocked from deleting
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteComment(@PathVariable int id) {
        // Get current authenticated user from JWT token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Authentication required");
            return ResponseEntity.status(401).body(error);
        }
        
        // Extract username from JWT token (set by JwtAuthenticationFilter)
        String username = authentication.getName();
        Users currentUser = usersRepository.findByUsername(username)
                .orElse(null);
        
        if (currentUser == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "User not found");
            return ResponseEntity.status(401).body(error);
        }
        
        // Check role - only ADMIN can delete comments
        String role = currentUser.getRole() != null ? currentUser.getRole() : "USER";
        if (!"ADMIN".equals(role)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Access denied. Only administrators can delete comments.");
            return ResponseEntity.status(403).body(error);
        }
        
        // Admin can delete comment
        if (comentsService.getCommentById(id).isPresent()) {
            comentsService.deleteComment(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Comment deleted successfully");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
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
