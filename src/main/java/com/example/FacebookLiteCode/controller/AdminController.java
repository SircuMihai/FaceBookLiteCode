package com.example.FacebookLiteCode.controller;

import com.example.FacebookLiteCode.dto.PostResponseDTO;
import com.example.FacebookLiteCode.dto.UserResponseDTO;
import com.example.FacebookLiteCode.repository.ComentsRepository;
import com.example.FacebookLiteCode.repository.LikeRepository;
import com.example.FacebookLiteCode.repository.PostRepository;
import com.example.FacebookLiteCode.repository.UsersRepository;
import com.example.FacebookLiteCode.services.PostService;
import com.example.FacebookLiteCode.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')") // All endpoints in this controller require ADMIN role
public class AdminController {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ComentsRepository comentsRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UsersService usersService;

    @Autowired
    private PostService postService;

    /**
     * Get admin dashboard statistics
     * Requires ADMIN role
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", usersRepository.count());
        stats.put("totalPosts", postRepository.count());
        stats.put("totalComments", comentsRepository.count());
        stats.put("totalLikes", likeRepository.count());
        return ResponseEntity.ok(stats);
    }

    /**
     * Get all users (admin view)
     * Requires ADMIN role
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = usersService.getAllUsersDTO();
        return ResponseEntity.ok(users);
    }

    /**
     * Search users by term (searches username, email, first name, last name)
     * Requires ADMIN role
     */
    @GetMapping("/users/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> searchUsers(@RequestParam String term) {
        // Search in username, email, first name, and last name
        List<UserResponseDTO> allUsers = usersService.getAllUsersDTO();
        List<UserResponseDTO> filtered = allUsers.stream()
                .filter(user -> 
                    (user.getUsername() != null && user.getUsername().toLowerCase().contains(term.toLowerCase())) ||
                    (user.getEmail() != null && user.getEmail().toLowerCase().contains(term.toLowerCase())) ||
                    (user.getFirstName() != null && user.getFirstName().toLowerCase().contains(term.toLowerCase())) ||
                    (user.getLastName() != null && user.getLastName().toLowerCase().contains(term.toLowerCase()))
                )
                .toList();
        return ResponseEntity.ok(filtered);
    }

    /**
     * Delete a user by ID
     * Requires ADMIN role
     */
    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable int userId) {
        if (usersService.getUserById(userId).isPresent()) {
            usersService.deleteUser(userId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User deleted successfully");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Get all posts (admin view)
     * Requires ADMIN role
     */
    @GetMapping("/posts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PostResponseDTO>> getAllPosts() {
        List<PostResponseDTO> posts = postService.getAllPostsDTO();
        return ResponseEntity.ok(posts);
    }

    /**
     * Delete a post by ID
     * Requires ADMIN role
     */
    @DeleteMapping("/posts/{postId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deletePost(@PathVariable int postId) {
        if (postService.getPostById(postId).isPresent()) {
            postService.deletePost(postId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Post deleted successfully");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }
}

