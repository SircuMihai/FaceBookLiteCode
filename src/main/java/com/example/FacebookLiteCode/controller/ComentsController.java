package com.example.FacebookLiteCode.controller;

import com.example.FacebookLiteCode.services.ComentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.FacebookLiteCode.dto.CommentRequestDTO;
import com.example.FacebookLiteCode.dto.CommentResponseDTO;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*")
public class ComentsController {
    
    @Autowired
    private ComentsService comentsService;
    
    @GetMapping
    public List<CommentResponseDTO> getAllComments() {
        return comentsService.getAllCommentsDTO();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseDTO> getCommentById(@PathVariable int id) {
        CommentResponseDTO dto = comentsService.getCommentResponseById(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }
    
    @PostMapping
    public ResponseEntity<CommentResponseDTO> createComment(@Valid @RequestBody CommentRequestDTO request) {
        return ResponseEntity.ok(comentsService.createComment(request));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CommentResponseDTO> updateComment(@PathVariable int id, @Valid @RequestBody CommentRequestDTO request) {
        return comentsService.updateComment(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
    public List<CommentResponseDTO> getCommentsByPostId(@PathVariable int postId) {
        return comentsService.getCommentsByPostIdDTO(postId);
    }
    
    @GetMapping("/user/{userId}")
    public List<CommentResponseDTO> getCommentsByUserId(@PathVariable int userId) {
        return comentsService.getCommentsByUserIdDTO(userId);
    }
    
    @GetMapping("/search/content/{content}")
    public List<CommentResponseDTO> searchCommentsByContent(@PathVariable String content) {
        return comentsService.getCommentsByContentDTO(content);
    }
}
