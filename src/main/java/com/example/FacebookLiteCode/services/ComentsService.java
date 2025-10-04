package com.example.FacebookLiteCode.services;

import com.example.FacebookLiteCode.model.Coments;
import com.example.FacebookLiteCode.model.Post;
import com.example.FacebookLiteCode.model.Users;
import com.example.FacebookLiteCode.repository.ComentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComentsService {
    
    @Autowired
    private ComentsRepository comentsRepository;
    
    public List<Coments> getAllComments() {
        return comentsRepository.findAll();
    }
    
    public Optional<Coments> getCommentById(int id) {
        return comentsRepository.findById(id);
    }
    
    public Coments saveComment(Coments comment) {
        return comentsRepository.save(comment);
    }
    
    public void deleteComment(int id) {
        comentsRepository.deleteById(id);
    }
    
    public List<Coments> getCommentsByPost(Post post) {
        return comentsRepository.findByPost(post);
    }
    
    public List<Coments> getCommentsByUser(Users user) {
        return comentsRepository.findByUser(user);
    }
    
    public List<Coments> getCommentsByPostId(int postId) {
        return comentsRepository.findByPostPostId(postId);
    }
    
    public List<Coments> getCommentsByUserId(int userId) {
        return comentsRepository.findByUserUserId(userId);
    }
    
    public List<Coments> getCommentsByContent(String content) {
        return comentsRepository.findByContentContainingIgnoreCase(content);
    }
}
