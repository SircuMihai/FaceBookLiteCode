package com.example.FacebookLiteCode.controller;

import com.example.FacebookLiteCode.services.FriendshipUserService;
import com.example.FacebookLiteCode.dto.FriendshipRequestDTO;
import com.example.FacebookLiteCode.dto.FriendshipResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/friendships")
@CrossOrigin(origins = "*")
public class FriendshipUserController {

    @Autowired
    private FriendshipUserService friendshipUserService;

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

    @PostMapping
    public ResponseEntity<FriendshipResponseDTO> createFriendship(@Valid @RequestBody FriendshipRequestDTO request) {
        try {
            FriendshipResponseDTO created = friendshipUserService.createFriendship(request);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<FriendshipResponseDTO> updateFriendship(@PathVariable int id, @Valid @RequestBody FriendshipRequestDTO request) {
        try {
            return friendshipUserService.updateFriendship(id, request)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFriendship(@PathVariable int id) {
        if (friendshipUserService.getFriendshipById(id).isPresent()) {
            friendshipUserService.deleteFriendship(id);
            return ResponseEntity.ok().build();
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
    @PostMapping("/friend-request")
    public ResponseEntity<FriendshipResponseDTO> sendFriendRequest(@RequestBody FriendshipRequestDTO request) {
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

    @PutMapping("/{id}/accept")
    public ResponseEntity<FriendshipResponseDTO> acceptFriendRequest(@PathVariable int id) {
        try {
            return friendshipUserService.acceptFriendship(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}/decline")
    public ResponseEntity<Void> declineFriendRequest(@PathVariable int id) {
        if (friendshipUserService.getFriendshipById(id).isPresent()) {
            friendshipUserService.deleteFriendship(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}/remove")
    public ResponseEntity<Void> removeFriend(@PathVariable int id) {
        if (friendshipUserService.getFriendshipById(id).isPresent()) {
            friendshipUserService.deleteFriendship(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
