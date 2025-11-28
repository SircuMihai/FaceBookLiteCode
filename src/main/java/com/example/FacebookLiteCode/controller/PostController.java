package com.example.FacebookLiteCode.controller;

import com.example.FacebookLiteCode.services.PostService;
import com.example.FacebookLiteCode.services.LikeService;
import com.example.FacebookLiteCode.repository.UsersRepository;
import com.example.FacebookLiteCode.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.example.FacebookLiteCode.dto.PostRequestDTO;
import com.example.FacebookLiteCode.dto.PostResponseDTO;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {

    @Autowired
    private PostService postService;
    
    @Autowired
    private LikeService likeService;
    
    @Autowired
    private UsersRepository usersRepository;

    @GetMapping
    public List<PostResponseDTO> getAllPosts() {
        return postService.getAllPostsDTO();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDTO> getPostById(@PathVariable int id) {
        PostResponseDTO post = postService.getPostResponseById(id);
        if (post == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(post);
    }

    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(@Valid @RequestBody PostRequestDTO request) {
        return ResponseEntity.ok(postService.createPost(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDTO> updatePost(@PathVariable int id, @Valid @RequestBody PostRequestDTO request) {
        return postService.updatePost(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete post - Only ADMIN role can delete posts
     * Regular users (USER role) are blocked from deleting
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePost(@PathVariable int id) {
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
        
        // Check role - only ADMIN can delete posts
        String role = currentUser.getRole() != null ? currentUser.getRole() : "USER";
        if (!"ADMIN".equals(role)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Access denied. Only administrators can delete posts.");
            return ResponseEntity.status(403).body(error);
        }
        
        // Admin can delete post
        if (postService.getPostById(id).isPresent()) {
            postService.deletePost(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Post deleted successfully");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}")
    public List<PostResponseDTO> getPostsByUserId(@PathVariable int userId) {
        return postService.getPostsByUserIdDTO(userId);
    }

    @GetMapping("/search/content/{content}")
    public List<PostResponseDTO> searchPostsByContent(@PathVariable String content) {
        return postService.getPostsByContentDTO(content);
    }

    @GetMapping("/search/date/{date}")
    public List<PostResponseDTO> searchPostsByDate(@PathVariable String date) {
        return postService.getPostsByDateDTO(date);
    }
    
    @PostMapping("/{id}/toggle-like")
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable int id, @RequestParam int userId) {
        try {
            boolean isLiked = likeService.toggleLike(id, userId);
            long likeCount = likeService.getLikeCount(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("isLiked", isLiked);
            response.put("likeCount", likeCount);
            
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}/like-status")
    public ResponseEntity<Map<String, Object>> getLikeStatus(@PathVariable int id, @RequestParam int userId) {
        try {
            boolean hasLiked = likeService.hasUserLiked(id, userId);
            long likeCount = likeService.getLikeCount(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("hasLiked", hasLiked);
            response.put("likeCount", likeCount);
            
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}
