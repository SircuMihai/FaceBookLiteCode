package com.example.FacebookLiteCode.controller;

import com.example.FacebookLiteCode.model.Users;
import com.example.FacebookLiteCode.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Setup controller for initial admin creation.
 * This endpoint should be secured or removed in production.
 * For development/testing purposes only.
 */
@RestController
@RequestMapping("/api/setup")
@CrossOrigin(origins = "*")
public class SetupController {

    @Autowired
    private UsersRepository usersRepository;

    /**
     * Promote a user to admin by username.
     * WARNING: This endpoint should be secured in production!
     * For now, it's open for initial setup.
     * 
     * @param username The username to promote to admin
     * @return Response with success or error message
     */
    @PutMapping("/promote/{username}")
    public ResponseEntity<?> promoteToAdmin(@PathVariable String username) {
        Users user = usersRepository.findByUsername(username)
                .orElse(null);
        
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        if ("ADMIN".equals(user.getRole())) {
            return ResponseEntity.badRequest().body(Map.of("error", "User is already an admin"));
        }
        
        user.setRole("ADMIN");
        usersRepository.save(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User promoted to admin successfully");
        response.put("username", user.getUsername());
        response.put("userId", user.getUserId());
        response.put("role", user.getRole());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Promote a user to admin by user ID.
     * 
     * @param userId The user ID to promote to admin
     * @return Response with success or error message
     */
    @PutMapping("/promote-by-id/{userId}")
    public ResponseEntity<?> promoteToAdminById(@PathVariable int userId) {
        Users user = usersRepository.findById(userId)
                .orElse(null);
        
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        if ("ADMIN".equals(user.getRole())) {
            return ResponseEntity.badRequest().body(Map.of("error", "User is already an admin"));
        }
        
        user.setRole("ADMIN");
        usersRepository.save(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User promoted to admin successfully");
        response.put("username", user.getUsername());
        response.put("userId", user.getUserId());
        response.put("role", user.getRole());
        
        return ResponseEntity.ok(response);
    }
}

