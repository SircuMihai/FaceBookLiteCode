package com.example.FacebookLiteCode.controller;

import com.example.FacebookLiteCode.dto.UserResponseDTO;
import com.example.FacebookLiteCode.dto.PostResponseDTO;
import com.example.FacebookLiteCode.dto.mapper.UserMapper;
import com.example.FacebookLiteCode.dto.mapper.PostMapper;
import com.example.FacebookLiteCode.model.Users;
import com.example.FacebookLiteCode.repository.UsersRepository;
import com.example.FacebookLiteCode.repository.PostRepository;
import com.example.FacebookLiteCode.repository.ComentsRepository;
import com.example.FacebookLiteCode.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
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
    private UserMapper userMapper;

    @Autowired
    private PostMapper postMapper;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalUsers", usersRepository.count());
        stats.put("totalPosts", postRepository.count());
        stats.put("totalComments", comentsRepository.count());
        stats.put("totalLikes", likeRepository.count());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<Users> users = usersRepository.findAll();
        List<UserResponseDTO> userDTOs = users.stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/users/search")
    public ResponseEntity<List<UserResponseDTO>> searchUsers(@RequestParam String term) {
        List<Users> users = usersRepository.findAll().stream()
                .filter(user -> 
                    user.getUsername().toLowerCase().contains(term.toLowerCase()) ||
                    (user.getFirstName() != null && user.getFirstName().toLowerCase().contains(term.toLowerCase())) ||
                    (user.getLastName() != null && user.getLastName().toLowerCase().contains(term.toLowerCase())) ||
                    (user.getEmail() != null && user.getEmail().toLowerCase().contains(term.toLowerCase()))
                )
                .collect(Collectors.toList());
        
        List<UserResponseDTO> userDTOs = users.stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable int userId) {
        // Prevent admin from deleting themselves
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Users currentUser = usersRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Current user not found"));
        
        if (currentUser.getUserId() == userId) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cannot delete your own account"));
        }

        if (usersRepository.existsById(userId)) {
            usersRepository.deleteById(userId);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/posts")
    public ResponseEntity<List<PostResponseDTO>> getAllPosts() {
        List<PostResponseDTO> posts = postRepository.findAll().stream()
                .map(postMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(posts);
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable int postId) {
        if (postRepository.existsById(postId)) {
            postRepository.deleteById(postId);
            return ResponseEntity.ok(Map.of("message", "Post deleted successfully"));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/users/{userId}/promote")
    public ResponseEntity<?> promoteToAdmin(@PathVariable int userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if ("ADMIN".equals(user.getRole())) {
            return ResponseEntity.badRequest().body(Map.of("error", "User is already an admin"));
        }
        
        user.setRole("ADMIN");
        usersRepository.save(user);
        
        return ResponseEntity.ok(Map.of("message", "User promoted to admin successfully", 
                                         "user", userMapper.toResponseDTO(user)));
    }

    @PutMapping("/users/{userId}/demote")
    public ResponseEntity<?> demoteFromAdmin(@PathVariable int userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Users currentUser = usersRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Current user not found"));
        
        // Prevent admin from demoting themselves
        if (currentUser.getUserId() == userId) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cannot demote your own account"));
        }
        
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!"ADMIN".equals(user.getRole())) {
            return ResponseEntity.badRequest().body(Map.of("error", "User is not an admin"));
        }
        
        user.setRole("USER");
        usersRepository.save(user);
        
        return ResponseEntity.ok(Map.of("message", "User demoted from admin successfully", 
                                         "user", userMapper.toResponseDTO(user)));
    }
}


