package com.example.FacebookLiteCode.controller;

import com.example.FacebookLiteCode.services.UsersService;
import com.example.FacebookLiteCode.repository.UsersRepository;
import com.example.FacebookLiteCode.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.example.FacebookLiteCode.dto.UserRequestDTO;
import com.example.FacebookLiteCode.dto.UpdateUserRequestDTO;
import com.example.FacebookLiteCode.dto.UserResponseDTO;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UsersController {

    @Autowired
    private UsersService usersService;
    
    @Autowired
    private UsersRepository usersRepository;

    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        return usersService.getAllUsersDTO();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable int id) {
        UserResponseDTO user = usersService.getUserResponseById(id);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO request) {
        return ResponseEntity.ok(usersService.createUser(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable int id, @Valid @RequestBody UpdateUserRequestDTO request) {
        return usersService.updateUser(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete user - Only ADMIN role can delete users
     * Regular users (USER role) are blocked from deleting
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable int id) {
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
        
        // Check role - only ADMIN can delete users
        String role = currentUser.getRole() != null ? currentUser.getRole() : "USER";
        if (!"ADMIN".equals(role)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Access denied. Only administrators can delete users.");
            return ResponseEntity.status(403).body(error);
        }
        
        // Admin can delete user
        if (usersService.getUserById(id).isPresent()) {
            usersService.deleteUser(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User deleted successfully");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDTO> getUserByUsername(@PathVariable String username) {
        return usersService.findByUsernameDTO(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        return usersService.findByEmailDTO(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/private/{privateAccount}")
    public List<UserResponseDTO> getUsersByPrivateAccount(@PathVariable boolean privateAccount) {
        return usersService.findByPrivateAccountDTO(privateAccount);
    }

    @GetMapping("/search/firstname/{firstName}")
    public List<UserResponseDTO> searchUsersByFirstName(@PathVariable String firstName) {
        return usersService.findByFirstNameContainingDTO(firstName);
    }

    @GetMapping("/search/lastname/{lastName}")
    public List<UserResponseDTO> searchUsersByLastName(@PathVariable String lastName) {
        return usersService.findByLastNameContainingDTO(lastName);
    }
}
