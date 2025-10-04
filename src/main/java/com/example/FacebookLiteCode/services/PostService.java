package com.example.FacebookLiteCode.services;

import com.example.FacebookLiteCode.model.Post;
import com.example.FacebookLiteCode.model.Users;
import com.example.FacebookLiteCode.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    
    @Autowired
    private PostRepository postRepository;
    
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }
    
    public Optional<Post> getPostById(int id) {
        return postRepository.findById(id);
    }
    
    public Post savePost(Post post) {
        return postRepository.save(post);
    }
    
    public void deletePost(int id) {
        postRepository.deleteById(id);
    }
    
    public List<Post> getPostsByUser(Users user) {
        return postRepository.findByUser(user);
    }
    
    public List<Post> getPostsByUserId(int userId) {
        return postRepository.findByUserUserId(userId);
    }
    
    public List<Post> getPostsByContent(String content) {
        return postRepository.findByContentContainingIgnoreCase(content);
    }
    
    public List<Post> getPostsByDate(String date) {
        return postRepository.findByCreatedAtContaining(date);
    }
}
