package com.example.FacebookLiteCode.controller;

import com.example.FacebookLiteCode.services.FriendshipUserService;
import com.example.FacebookLiteCode.repository.UsersRepository;
import com.example.FacebookLiteCode.model.Users;
import com.example.FacebookLiteCode.dto.FriendshipRequestDTO;
import com.example.FacebookLiteCode.dto.FriendshipResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/friendships")
@CrossOrigin(origins = "*")
public class FriendshipUserController {

    @Autowired
    private FriendshipUserService friendshipUserService;
    
    @Autowired
    private UsersRepository usersRepository;

    @GetMapping
    public List<FriendshipResponseDTO> getAllFriendships() {
        return friendshipUserService.getAllFriendshipsDTO();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FriendshipResponseDTO> getFriendshipById(@PathVariable int id) {
        FriendshipResponseDTO dto = friendshipUserService.getFriendshipResponseById(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    /**
     * Create friendship - Only ADMIN role can create friendships
     * Regular users (USER role) are blocked
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createFriendship(@Valid @RequestBody FriendshipRequestDTO request) {
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
        
        // Check role - only ADMIN can create friendships
        String role = currentUser.getRole() != null ? currentUser.getRole() : "USER";
        if (!"ADMIN".equals(role)) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Access denied. Only administrators can create friendships.");
            return ResponseEntity.status(403).body(error);
        }
        
        try {
            FriendshipResponseDTO created = friendshipUserService.createFriendship(request);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update friendship - Only ADMIN role can update friendships
     * Regular users (USER role) are blocked
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateFriendship(@PathVariable int id, @Valid @RequestBody FriendshipRequestDTO request) {
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
        
        // Check role - only ADMIN can update friendships
        String role = currentUser.getRole() != null ? currentUser.getRole() : "USER";
        if (!"ADMIN".equals(role)) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Access denied. Only administrators can update friendships.");
            return ResponseEntity.status(403).body(error);
        }
        
        try {
            return friendshipUserService.updateFriendship(id, request)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete friendship - Only ADMIN role can delete friendships
     * Regular users (USER role) are blocked from deleting
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteFriendship(@PathVariable int id) {
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
        
        // Check role - only ADMIN can delete friendships
        String role = currentUser.getRole() != null ? currentUser.getRole() : "USER";
        if (!"ADMIN".equals(role)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Access denied. Only administrators can delete friendships.");
            return ResponseEntity.status(403).body(error);
        }
        
        // Admin can delete friendship
        if (friendshipUserService.getFriendshipById(id).isPresent()) {
            friendshipUserService.deleteFriendship(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Friendship deleted successfully");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/user1/{user1Id}")
    public List<FriendshipResponseDTO> getByUser1(@PathVariable int user1Id) {
        return friendshipUserService.getFriendshipsByUser1IdDTO(user1Id);
    }

    @GetMapping("/user2/{user2Id}")
    public List<FriendshipResponseDTO> getByUser2(@PathVariable int user2Id) {
        return friendshipUserService.getFriendshipsByUser2IdDTO(user2Id);
    }

    @GetMapping("/between/{user1Id}/{user2Id}")
    public ResponseEntity<FriendshipResponseDTO> getBetween(@PathVariable int user1Id, @PathVariable int user2Id) {
        return friendshipUserService.getFriendshipBetweenUsersDTO(user1Id, user2Id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public List<FriendshipResponseDTO> getByStatus(@PathVariable String status) {
        return friendshipUserService.getFriendshipsByStatusDTO(status);
    }

    // Friend request endpoints
    /**
     * Send friend request - Only ADMIN role can send friend requests
     * Regular users (USER role) are blocked
     */
    @PostMapping("/friend-request")
    public ResponseEntity<Map<String, Object>> sendFriendRequest(@RequestBody FriendshipRequestDTO request) {
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
        
        // Check role - only ADMIN can send friend requests
        String role = currentUser.getRole() != null ? currentUser.getRole() : "USER";
        if (!"ADMIN".equals(role)) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Access denied. Only administrators can send friend requests.");
            return ResponseEntity.status(403).body(error);
        }
        
        try {
            FriendshipResponseDTO created = friendshipUserService.createFriendship(request);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/requests/{userId}")
    public List<FriendshipResponseDTO> getFriendRequests(@PathVariable int userId) {
        return friendshipUserService.getFriendRequestsDTO(userId);
    }

    @GetMapping("/friends/{userId}")
    public List<FriendshipResponseDTO> getFriends(@PathVariable int userId) {
        return friendshipUserService.getFriendsDTO(userId);
    }

    /**
     * Accept friend request - Only ADMIN role can accept friend requests
     * Regular users (USER role) are blocked
     */
    @PutMapping("/{id}/accept")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable int id) {
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
        
        // Check role - only ADMIN can accept friend requests
        String role = currentUser.getRole() != null ? currentUser.getRole() : "USER";
        if (!"ADMIN".equals(role)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Access denied. Only administrators can accept friend requests.");
            return ResponseEntity.status(403).body(error);
        }
        
        try {
            Optional<FriendshipResponseDTO> result = friendshipUserService.acceptFriendship(id);
            if (result.isPresent()) {
                return ResponseEntity.ok(result.get());
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Friend request not found or already processed");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (IllegalArgumentException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage() != null ? ex.getMessage() : "Invalid friend request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to accept friend request: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Decline friend request - Only ADMIN role can decline friend requests
     * Regular users (USER role) are blocked
     */
    @DeleteMapping("/{id}/decline")
    public ResponseEntity<?> declineFriendRequest(@PathVariable int id) {
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
        
        // Check role - only ADMIN can decline friend requests
        String role = currentUser.getRole() != null ? currentUser.getRole() : "USER";
        if (!"ADMIN".equals(role)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Access denied. Only administrators can decline friend requests.");
            return ResponseEntity.status(403).body(error);
        }
        
        try {
            if (friendshipUserService.getFriendshipById(id).isPresent()) {
                friendshipUserService.deleteFriendship(id);
                Map<String, String> success = new HashMap<>();
                success.put("message", "Friend request declined successfully");
                return ResponseEntity.ok(success);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Friend request not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to decline friend request: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Remove friend - Only ADMIN role can remove friends
     * Regular users (USER role) are blocked
     */
    @DeleteMapping("/{id}/remove")
    public ResponseEntity<Map<String, String>> removeFriend(@PathVariable int id) {
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
        
        // Check role - only ADMIN can remove friends
        String role = currentUser.getRole() != null ? currentUser.getRole() : "USER";
        if (!"ADMIN".equals(role)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Access denied. Only administrators can remove friends.");
            return ResponseEntity.status(403).body(error);
        }
        
        // Admin can remove friend
        if (friendshipUserService.getFriendshipById(id).isPresent()) {
            friendshipUserService.deleteFriendship(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Friend removed successfully");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }
}
