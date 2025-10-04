package com.example.FacebookLiteCode.controller;

import com.example.FacebookLiteCode.model.Coments;
import com.example.FacebookLiteCode.services.ComentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*")
public class ComentsController {
    
    @Autowired
    private ComentsService comentsService;
    
    @GetMapping
    public List<Coments> getAllComments() {
        return comentsService.getAllComments();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Coments> getCommentById(@PathVariable int id) {
        Optional<Coments> comment = comentsService.getCommentById(id);
        return comment.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Coments createComment(@RequestBody Coments comment) {
        return comentsService.saveComment(comment);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Coments> updateComment(@PathVariable int id, @RequestBody Coments comment) {
        if (comentsService.getCommentById(id).isPresent()) {
            comment.setComment_id(id);
            return ResponseEntity.ok(comentsService.saveComment(comment));
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable int id) {
        if (comentsService.getCommentById(id).isPresent()) {
            comentsService.deleteComment(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/post/{postId}")
    public List<Coments> getCommentsByPostId(@PathVariable int postId) {
        return comentsService.getCommentsByPostId(postId);
    }
    
    @GetMapping("/user/{userId}")
    public List<Coments> getCommentsByUserId(@PathVariable int userId) {
        return comentsService.getCommentsByUserId(userId);
    }
    
    @GetMapping("/search/content/{content}")
    public List<Coments> searchCommentsByContent(@PathVariable String content) {
        return comentsService.getCommentsByContent(content);
    }
}
