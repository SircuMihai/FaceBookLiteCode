package com.example.FacebookLiteCode.controller;

import com.example.FacebookLiteCode.dto.CreatePostRequest;
import com.example.FacebookLiteCode.model.Post;
import com.example.FacebookLiteCode.model.Users;
import com.example.FacebookLiteCode.repository.UsersRepository;
import com.example.FacebookLiteCode.services.PostService;
import com.example.FacebookLiteCode.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/simple")
@CrossOrigin(origins = "*")
public class SimplePostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UsersService usersService;
    
    @Autowired
    private UsersRepository usersRepository;

    /**
     * Create post (simple) - Only ADMIN role can create posts
     * Regular users (USER role) are blocked
     */
    @PostMapping("/create-post")
    public ResponseEntity<Map<String, Object>> createPost(@RequestBody CreatePostRequest request) {
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
        
        // Check role - only ADMIN can create posts
        String role = currentUser.getRole() != null ? currentUser.getRole() : "USER";
        if (!"ADMIN".equals(role)) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Access denied. Only administrators can create posts.");
            return ResponseEntity.status(403).body(error);
        }
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("=== SIMPLE POST CREATION ===");
            System.out.println("Request: " + request);
            
            // Check if user exists
            Optional<Users> userOpt = usersService.getUserById(request.getUserId());
            if (userOpt.isEmpty()) {
                response.put("error", "User not found with ID: " + request.getUserId());
                return ResponseEntity.badRequest().body(response);
            }
            
            Users user = userOpt.get();
            System.out.println("User found: " + user.getUserId() + " - " + user.getUsername());
            
            // Create post
            Post post = new Post();
            post.setContent(request.getContent());
            post.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            // Set user - this is the critical part
            post.setUser(user);
            
            System.out.println("Post before save:");
            System.out.println("- Content: " + post.getContent());
            System.out.println("- CreatedAt: " + post.getCreatedAt());
            System.out.println("- User: " + (post.getUser() != null ? post.getUser().getUserId() : "NULL"));
            
            // Save post
            Post savedPost = postService.savePost(post);
            
            System.out.println("Post saved successfully:");
            System.out.println("- Post ID: " + savedPost.getPostId());
            System.out.println("- Content: " + savedPost.getContent());
            System.out.println("- User ID: " + (savedPost.getUser() != null ? savedPost.getUser().getUserId() : "NULL"));
            
            response.put("success", true);
            response.put("postId", savedPost.getPostId());
            response.put("content", savedPost.getContent());
            response.put("userId", savedPost.getUser() != null ? savedPost.getUser().getUserId() : null);
            response.put("message", "Post created successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/test")
    public Map<String, Object> test() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "Simple controller working");
        result.put("users", usersService.getAllUsers().size());
        return result;
    }
}
