package com.example.FacebookLiteCode.controller;

import com.example.FacebookLiteCode.services.PostService;
import com.example.FacebookLiteCode.services.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.FacebookLiteCode.dto.PostRequestDTO;
import com.example.FacebookLiteCode.dto.PostResponseDTO;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {

    @Autowired
    private PostService postService;
    
    @Autowired
    private LikeService likeService;

    @GetMapping
    public List<PostResponseDTO> getAllPosts() {
        return postService.getAllPostsDTO();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDTO> getPostById(@PathVariable int id) {
        PostResponseDTO post = postService.getPostResponseById(id);
        if (post == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(post);
    }

    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(@Valid @RequestBody PostRequestDTO request) {
        return ResponseEntity.ok(postService.createPost(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDTO> updatePost(@PathVariable int id, @Valid @RequestBody PostRequestDTO request) {
        return postService.updatePost(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable int id) {
        if (postService.getPostById(id).isPresent()) {
            postService.deletePost(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}")
    public List<PostResponseDTO> getPostsByUserId(@PathVariable int userId) {
        return postService.getPostsByUserIdDTO(userId);
    }

    @GetMapping("/search/content/{content}")
    public List<PostResponseDTO> searchPostsByContent(@PathVariable String content) {
        return postService.getPostsByContentDTO(content);
    }

    @GetMapping("/search/date/{date}")
    public List<PostResponseDTO> searchPostsByDate(@PathVariable String date) {
        return postService.getPostsByDateDTO(date);
    }
    
    @PostMapping("/{id}/toggle-like")
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable int id, @RequestParam int userId) {
        try {
            boolean isLiked = likeService.toggleLike(id, userId);
            long likeCount = likeService.getLikeCount(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("isLiked", isLiked);
            response.put("likeCount", likeCount);
            
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}/like-status")
    public ResponseEntity<Map<String, Object>> getLikeStatus(@PathVariable int id, @RequestParam int userId) {
        try {
            boolean hasLiked = likeService.hasUserLiked(id, userId);
            long likeCount = likeService.getLikeCount(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("hasLiked", hasLiked);
            response.put("likeCount", likeCount);
            
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}
