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

import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
     * Delete a post - Users can only delete their own posts
     * Admins can delete any post via /api/admin/posts/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePost(@PathVariable int id) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        
        String username = authentication.getName();
        Users currentUser = usersRepository.findByUsername(username)
                .orElse(null);
        
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        
        // Check if post exists
        PostResponseDTO post = postService.getPostResponseById(id);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Check if user is the owner of the post OR is an admin
        boolean isOwner = post.getUserId() == currentUser.getUserId();
        boolean isAdmin = "ADMIN".equals(currentUser.getRole());
        
        if (!isOwner && !isAdmin) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "You can only delete your own posts");
            return ResponseEntity.status(403).body(error);
        }
        
        // Delete the post
        postService.deletePost(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Post deleted successfully");
        return ResponseEntity.ok(response);
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
