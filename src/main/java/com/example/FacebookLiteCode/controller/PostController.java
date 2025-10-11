package com.example.FacebookLiteCode.controller;

import com.example.FacebookLiteCode.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.FacebookLiteCode.dto.PostRequestDTO;
import com.example.FacebookLiteCode.dto.PostResponseDTO;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {

    @Autowired
    private PostService postService;

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
}
