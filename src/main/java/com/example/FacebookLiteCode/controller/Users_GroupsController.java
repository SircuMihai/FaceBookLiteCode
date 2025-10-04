package com.example.FacebookLiteCode.controller;

import com.example.FacebookLiteCode.model.Users_Groups;
import com.example.FacebookLiteCode.services.Users_GroupsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users-groups")
@CrossOrigin(origins = "*")
public class Users_GroupsController {
    
    @Autowired
    private Users_GroupsService users_GroupsService;
    
    @GetMapping
    public List<Users_Groups> getAllUsersGroups() {
        return users_GroupsService.getAllUsersGroups();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Users_Groups> getUsersGroupsById(@PathVariable int id) {
        Optional<Users_Groups> usersGroups = users_GroupsService.getUsersGroupsById(id);
        return usersGroups.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Users_Groups createUsersGroups(@RequestBody Users_Groups usersGroups) {
        return users_GroupsService.saveUsersGroups(usersGroups);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Users_Groups> updateUsersGroups(@PathVariable int id, @RequestBody Users_Groups usersGroups) {
        if (users_GroupsService.getUsersGroupsById(id).isPresent()) {
            usersGroups.setUsersGroupsId(id);
            return ResponseEntity.ok(users_GroupsService.saveUsersGroups(usersGroups));
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsersGroups(@PathVariable int id) {
        if (users_GroupsService.getUsersGroupsById(id).isPresent()) {
            users_GroupsService.deleteUsersGroups(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/user/{userId}")
    public List<Users_Groups> getUsersGroupsByUserId(@PathVariable int userId) {
        return users_GroupsService.getUsersGroupsByUserId(userId);
    }
    
    @GetMapping("/group/{groupId}")
    public List<Users_Groups> getUsersGroupsByGroupId(@PathVariable int groupId) {
        return users_GroupsService.getUsersGroupsByGroupId(groupId);
    }
    
    @GetMapping("/admin/{groupAdmin}")
    public List<Users_Groups> getUsersGroupsByGroupAdmin(@PathVariable boolean groupAdmin) {
        return users_GroupsService.getUsersGroupsByGroupAdmin(groupAdmin);
    }
    
    @GetMapping("/user/{userId}/group/{groupId}")
    public ResponseEntity<Users_Groups> getUsersGroupsByUserIdAndGroupId(@PathVariable int userId, @PathVariable int groupId) {
        Users_Groups usersGroups = users_GroupsService.getUsersGroupsByUserIdAndGroupId(userId, groupId);
        return usersGroups != null ? ResponseEntity.ok(usersGroups) : ResponseEntity.notFound().build();
    }
}
