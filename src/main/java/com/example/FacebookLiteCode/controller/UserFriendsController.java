package com.example.FacebookLiteCode.controller;

import com.example.FacebookLiteCode.model.UserFriends;
import com.example.FacebookLiteCode.services.UserFriendsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user-friends")
@CrossOrigin(origins = "*")
public class UserFriendsController {
    
    @Autowired
    private UserFriendsService userFriendsService;
    
    @GetMapping
    public List<UserFriends> getAllUserFriends() {
        return userFriendsService.getAllUserFriends();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserFriends> getUserFriendsById(@PathVariable int id) {
        Optional<UserFriends> userFriends = userFriendsService.getUserFriendsById(id);
        return userFriends.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public UserFriends createUserFriends(@RequestBody UserFriends userFriends) {
        return userFriendsService.saveUserFriends(userFriends);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UserFriends> updateUserFriends(@PathVariable int id, @RequestBody UserFriends userFriends) {
        if (userFriendsService.getUserFriendsById(id).isPresent()) {
            userFriends.setUserFriendsId(id);
            return ResponseEntity.ok(userFriendsService.saveUserFriends(userFriends));
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserFriends(@PathVariable int id) {
        if (userFriendsService.getUserFriendsById(id).isPresent()) {
            userFriendsService.deleteUserFriends(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/user/{userId}")
    public List<UserFriends> getUserFriendsByUserId(@PathVariable int userId) {
        return userFriendsService.getUserFriendsByUserId(userId);
    }
    
    @GetMapping("/friend/{friendId}")
    public List<UserFriends> getUserFriendsByFriendId(@PathVariable int friendId) {
        return userFriendsService.getUserFriendsByFriendId(friendId);
    }
    
    @GetMapping("/user/{userId}/friend/{friendId}")
    public ResponseEntity<UserFriends> getUserFriendsByUserIdAndFriendId(@PathVariable int userId, @PathVariable int friendId) {
        UserFriends userFriends = userFriendsService.getUserFriendsByUserIdAndFriendId(userId, friendId);
        return userFriends != null ? ResponseEntity.ok(userFriends) : ResponseEntity.notFound().build();
    }
    
    @GetMapping("/connections/{userId}")
    public List<UserFriends> getUserConnections(@PathVariable int userId) {
        return userFriendsService.getUserFriendsByUserIdOrFriendId(userId, userId);
    }
}
