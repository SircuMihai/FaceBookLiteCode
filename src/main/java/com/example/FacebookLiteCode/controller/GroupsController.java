package com.example.FacebookLiteCode.controller;

import com.example.FacebookLiteCode.model.Groups;
import com.example.FacebookLiteCode.services.GroupsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = "*")
public class GroupsController {
    
    @Autowired
    private GroupsService groupsService;
    
    @GetMapping
    public List<Groups> getAllGroups() {
        return groupsService.getAllGroups();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Groups> getGroupById(@PathVariable int id) {
        Optional<Groups> group = groupsService.getGroupById(id);
        return group.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Groups createGroup(@RequestBody Groups group) {
        return groupsService.saveGroup(group);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Groups> updateGroup(@PathVariable int id, @RequestBody Groups group) {
        if (groupsService.getGroupById(id).isPresent()) {
            group.setGroup_id(id);
            return ResponseEntity.ok(groupsService.saveGroup(group));
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable int id) {
        if (groupsService.getGroupById(id).isPresent()) {
            groupsService.deleteGroup(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/search/name/{groupName}")
    public List<Groups> searchGroupsByName(@PathVariable String groupName) {
        return groupsService.getGroupsByName(groupName);
    }
    
    @GetMapping("/privacy/{privacy}")
    public List<Groups> getGroupsByPrivacy(@PathVariable String privacy) {
        return groupsService.getGroupsByPrivacy(privacy);
    }
    
    @GetMapping("/name/{groupName}")
    public ResponseEntity<Groups> getGroupByName(@PathVariable String groupName) {
        Groups group = groupsService.getGroupByName(groupName);
        return group != null ? ResponseEntity.ok(group) : ResponseEntity.notFound().build();
    }
}
