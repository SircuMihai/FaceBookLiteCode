package com.example.FacebookLiteCode.controller;

import com.example.FacebookLiteCode.model.FriendshipUser;
import com.example.FacebookLiteCode.services.FriendshipUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/friendships")
@CrossOrigin(origins = "*")
public class FriendshipUserController {

    @Autowired
    private FriendshipUserService friendshipUserService;

    @GetMapping
    public List<FriendshipUser> getAllFriendships() {
        return friendshipUserService.getAllFriendships();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FriendshipUser> getFriendshipById(@PathVariable int id) {
        Optional<FriendshipUser> fs = friendshipUserService.getFriendshipById(id);
        return fs.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public FriendshipUser createFriendship(@RequestBody FriendshipUser friendship) {
        return friendshipUserService.saveFriendship(friendship);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FriendshipUser> updateFriendship(@PathVariable int id, @RequestBody FriendshipUser friendship) {
        if (friendshipUserService.getFriendshipById(id).isPresent()) {
            friendship.setFrienshipId(id);
            return ResponseEntity.ok(friendshipUserService.saveFriendship(friendship));
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
    public List<FriendshipUser> getByUser1(@PathVariable int user1Id) {
        return friendshipUserService.getFriendshipsByUser1Id(user1Id);
    }

    @GetMapping("/user2/{user2Id}")
    public List<FriendshipUser> getByUser2(@PathVariable int user2Id) {
        return friendshipUserService.getFriendshipsByUser2Id(user2Id);
    }

    @GetMapping("/between/{user1Id}/{user2Id}")
    public ResponseEntity<FriendshipUser> getBetween(@PathVariable int user1Id, @PathVariable int user2Id) {
        return friendshipUserService.getFriendshipBetweenUsers(user1Id, user2Id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public List<FriendshipUser> getByStatus(@PathVariable String status) {
        return friendshipUserService.getFriendshipsByStatus(status);
    }
}
