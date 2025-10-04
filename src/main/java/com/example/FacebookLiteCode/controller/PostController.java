package com.example.FacebookLiteCode.controller;

//import com.example.FacebookLiteCode.dto.*;
import com.example.FacebookLiteCode.model.Post;
import com.example.FacebookLiteCode.model.Users;
import com.example.FacebookLiteCode.services.PostService;
import com.example.FacebookLiteCode.services.UsersService;
//import com.example.FacebookLiteCode.services.MapperService;
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
    
    @Autowired
    private UsersService usersService;
    
//    @Autowired
//    private MapperService mapperService;
//
//    @GetMapping
//    public ApiResponseDto<List<PostDto>> getAllPosts() {
//        try {
//            List<Post> posts = postService.getAllPosts();
//            List<PostDto> postDtos = mapperService.toPostDtoList(posts);
//            return ApiResponseDto.success(postDtos, "Postări găsite cu succes");
//        } catch (Exception e) {
//            return ApiResponseDto.error("Eroare la preluarea postărilor: " + e.getMessage());
//        }
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Post> getPostById(@PathVariable int id) {
//        Optional<Post> post = postService.getPostById(id);
//        return post.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
//    }
//
//    @PostMapping
//    public ApiResponseDto<PostDto> createPost(@RequestBody PostRequestDto postRequestDto) {
//        try {
//            // Validări de bază
//            if (postRequestDto.getContent() == null || postRequestDto.getContent().trim().isEmpty()) {
//                return ApiResponseDto.error("Conținutul postării este obligatoriu");
//            }
//            if (postRequestDto.getUserId() == null) {
//                return ApiResponseDto.error("ID-ul utilizatorului este obligatoriu");
//            }
//
//            // Verificăm dacă utilizatorul există
//            Optional<Users> user = usersService.getUserById(postRequestDto.getUserId().intValue());
//            if (!user.isPresent()) {
//                return ApiResponseDto.error("Utilizatorul nu a fost găsit");
//            }
//
//            // Convertim DTO-ul la entitate
//            Post post = mapperService.toPostEntity(postRequestDto);
//            post.setUser(user.get());
//            post.setCreatedAt(java.time.LocalDateTime.now().toString());
//
//            // Salvăm postarea
//            Post savedPost = postService.savePost(post);
//
//            // Convertim înapoi la DTO pentru răspuns
//            PostDto postDto = mapperService.toPostDto(savedPost);
//
//            return ApiResponseDto.success(postDto, "Postare creată cu succes");
//        } catch (Exception e) {
//            return ApiResponseDto.error("Eroare la crearea postării: " + e.getMessage());
//        }
//    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable int id, @RequestBody Post post) {
        if (postService.getPostById(id).isPresent()) {
            post.setPostId(id);
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
