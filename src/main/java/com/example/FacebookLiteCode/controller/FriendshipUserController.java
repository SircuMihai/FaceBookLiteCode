package com.example.FacebookLiteCode.controller;

import com.example.FacebookLiteCode.model.FriendshipUser;
import com.example.FacebookLiteCode.services.FriendshipUserService;
import com.example.FacebookLiteCode.dto.FriendshipRequestDTO;
import com.example.FacebookLiteCode.dto.FriendshipResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/friendships")
@CrossOrigin(origins = "*")
public class FriendshipUserController {

    @Autowired
    private FriendshipUserService friendshipUserService;

    @GetMapping
    public List<FriendshipResponseDTO> getAllFriendships() {
        return friendshipUserService.getAllFriendships()
                .stream()
                .map(friendshipUserService::toResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FriendshipResponseDTO> getFriendshipById(@PathVariable int id) {
        Optional<FriendshipUser> fs = friendshipUserService.getFriendshipById(id);
        return fs.map(f -> ResponseEntity.ok(friendshipUserService.toResponseDTO(f)))
                 .orElse(ResponseEntity.notFound().build());
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
    public ResponseEntity<FriendshipResponseDTO> updateFriendship(@PathVariable int id, @RequestBody FriendshipUser friendship) {
        if (friendshipUserService.getFriendshipById(id).isPresent()) {
            friendship.setFrienshipId(id);
            FriendshipUser saved = friendshipUserService.saveFriendship(friendship);
            return ResponseEntity.ok(friendshipUserService.toResponseDTO(saved));
        }
        return ResponseEntity.notFound().build();
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
        return friendshipUserService.getFriendshipsByUser1Id(user1Id)
                .stream()
                .map(friendshipUserService::toResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/user2/{user2Id}")
    public List<FriendshipResponseDTO> getByUser2(@PathVariable int user2Id) {
        return friendshipUserService.getFriendshipsByUser2Id(user2Id)
                .stream()
                .map(friendshipUserService::toResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/between/{user1Id}/{user2Id}")
    public ResponseEntity<FriendshipResponseDTO> getBetween(@PathVariable int user1Id, @PathVariable int user2Id) {
        return friendshipUserService.getFriendshipBetweenUsers(user1Id, user2Id)
                .map(friendshipUserService::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public List<FriendshipResponseDTO> getByStatus(@PathVariable String status) {
        return friendshipUserService.getFriendshipsByStatus(status)
                .stream()
                .map(friendshipUserService::toResponseDTO)
                .collect(Collectors.toList());
    }
}
