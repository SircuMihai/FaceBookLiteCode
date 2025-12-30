package com.example.FacebookLiteCode.controller;

import com.example.FacebookLiteCode.dto.PostRequestDTO;
import com.example.FacebookLiteCode.dto.PostResponseDTO;
import com.example.FacebookLiteCode.model.Users;
import com.example.FacebookLiteCode.repository.UsersRepository;
import com.example.FacebookLiteCode.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/simple")
@CrossOrigin(origins = "*")
public class SimplePostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UsersRepository usersRepository;

    // Compatibility endpoint for frontend expecting /api/simple/create-post
    @PostMapping("/create-post")
    public ResponseEntity<?> createSimplePost(@RequestBody Map<String, Object> body) {
        // Auth check
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Authentication required");
            return ResponseEntity.status(401).body(error);
        }

        // Current user
        String username = authentication.getName();
        Users currentUser = usersRepository.findByUsername(username).orElse(null);
        if (currentUser == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "User not found");
            return ResponseEntity.status(401).body(error);
        }

        // Extract content
        Object contentObj = body != null ? body.get("content") : null;
        if (contentObj == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "content is required");
            return ResponseEntity.status(400).body(error);
        }
        String content = String.valueOf(contentObj);
        if (content.isBlank()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "content must not be blank");
            return ResponseEntity.status(400).body(error);
        }

        // Build request and delegate to PostService
        PostRequestDTO req = new PostRequestDTO();
        req.setContent(content);
        req.setUserId(currentUser.getUserId());

        PostResponseDTO created = postService.createPost(req);
        return ResponseEntity.ok(created);
    }
}
