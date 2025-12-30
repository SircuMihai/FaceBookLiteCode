package com.example.FacebookLiteCode.controller;

import com.example.FacebookLiteCode.services.FriendshipUserService;
import com.example.FacebookLiteCode.repository.UsersRepository;
import com.example.FacebookLiteCode.model.Users;
import com.example.FacebookLiteCode.model.FriendshipUser;
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
    public ResponseEntity<?> createFriendship(@Valid @RequestBody FriendshipRequestDTO request) {
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
        
        // Ownership and validation checks
        if (request.getUser1Id() == null || request.getUser2Id() == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "user1Id and user2Id are required");
            return ResponseEntity.status(400).body(error);
        }
        if (currentUser.getUserId() != request.getUser1Id()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "You can only create friendships as yourself (user1Id must match authenticated user)");
            return ResponseEntity.status(403).body(error);
        }
        if (request.getUser1Id().equals(request.getUser2Id())) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "You cannot send a friend request to yourself");
            return ResponseEntity.status(400).body(error);
        }
        if (request.getStatus() == null || request.getStatus().isBlank()) {
            request.setStatus("pending");
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
    public ResponseEntity<?> updateFriendship(@PathVariable int id, @Valid @RequestBody FriendshipRequestDTO request) {
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
    public ResponseEntity<?> sendFriendRequest(@RequestBody FriendshipRequestDTO request) {
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
        
        // Ownership and validation checks
        if (request.getUser1Id() == null || request.getUser2Id() == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "user1Id and user2Id are required");
            return ResponseEntity.status(400).body(error);
        }
        if (currentUser.getUserId() != request.getUser1Id()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "You can only send friend requests as yourself");
            return ResponseEntity.status(403).body(error);
        }
        if (request.getUser1Id().equals(request.getUser2Id())) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "You cannot send a friend request to yourself");
            return ResponseEntity.status(400).body(error);
        }
        if (request.getStatus() == null || request.getStatus().isBlank()) {
            request.setStatus("pending");
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
        
        // Authorization: only the recipient (user2) can accept
        Optional<FriendshipUser> friendshipOpt = friendshipUserService.getFriendshipById(id);
        if (friendshipOpt.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Friend request not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        FriendshipUser friendship = friendshipOpt.get();
        if (friendship.getUser2() == null || friendship.getUser2().getUserId() != currentUser.getUserId()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Access denied. Only the recipient can accept this request.");
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
        
        // Authorization: only the recipient (user2) can decline
        Optional<FriendshipUser> friendshipOpt = friendshipUserService.getFriendshipById(id);
        if (friendshipOpt.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Friend request not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        FriendshipUser friendship = friendshipOpt.get();
        if (friendship.getUser2() == null || friendship.getUser2().getUserId() != currentUser.getUserId()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Access denied. Only the recipient can decline this request.");
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
        
        // Authorization: only participants (user1 or user2) can remove
        Optional<FriendshipUser> friendshipOpt = friendshipUserService.getFriendshipById(id);
        if (friendshipOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        FriendshipUser friendship = friendshipOpt.get();
        int uid = currentUser.getUserId();
        if (friendship.getUser1() == null || friendship.getUser2() == null ||
            (friendship.getUser1().getUserId() != uid && friendship.getUser2().getUserId() != uid)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Access denied. Only friendship participants can remove it.");
            return ResponseEntity.status(403).body(error);
        }
        
        // Authorized participant can remove friend
        friendshipUserService.deleteFriendship(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Friend removed successfully");
        return ResponseEntity.ok(response);
    }
}
