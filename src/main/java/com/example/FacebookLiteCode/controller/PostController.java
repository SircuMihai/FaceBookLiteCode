package com.example.FacebookLiteCode.controller;

import com.example.FacebookLiteCode.model.Post;
import com.example.FacebookLiteCode.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {
    
    @Autowired
    private PostService postService;
    
    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable int id) {
        Optional<Post> post = postService.getPostById(id);
        return post.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Post createPost(@RequestBody Post post) {
        return postService.savePost(post);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable int id, @RequestBody Post post) {
        if (postService.getPostById(id).isPresent()) {
            post.setPost_id(id);
            return ResponseEntity.ok(postService.savePost(post));
        }
        return ResponseEntity.notFound().build();
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
    public List<Post> getPostsByUserId(@PathVariable int userId) {
        return postService.getPostsByUserId(userId);
    }
    
    @GetMapping("/search/content/{content}")
    public List<Post> searchPostsByContent(@PathVariable String content) {
        return postService.getPostsByContent(content);
    }
    
    @GetMapping("/search/date/{date}")
    public List<Post> searchPostsByDate(@PathVariable String date) {
        return postService.getPostsByDate(date);
    }
}
