package com.example.FacebookLiteCode.controller;

import com.example.FacebookLiteCode.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.FacebookLiteCode.dto.UserRequestDTO;
import com.example.FacebookLiteCode.dto.UpdateUserRequestDTO;
import com.example.FacebookLiteCode.dto.UserResponseDTO;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UsersController {

    @Autowired
    private UsersService usersService;

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
     * Delete user endpoint - REMOVED for security
     * User deletion should only be done through /api/admin/users/{userId}
     * which requires ADMIN role.
     * 
     * If you need user self-deletion, implement it separately with proper checks.
     */
    // @DeleteMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')") // This endpoint is disabled - use /api/admin/users/{id} instead
    // public ResponseEntity<Void> deleteUser(@PathVariable int id) {
    //     if (usersService.getUserById(id).isPresent()) {
    //         usersService.deleteUser(id);
    //         return ResponseEntity.ok().build();
    //     }
    //     return ResponseEntity.notFound().build();
    // }

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
