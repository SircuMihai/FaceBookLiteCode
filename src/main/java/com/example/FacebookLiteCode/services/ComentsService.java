package com.example.FacebookLiteCode.services;

import com.example.FacebookLiteCode.model.Coments;
import com.example.FacebookLiteCode.model.Post;
import com.example.FacebookLiteCode.model.Users;
import com.example.FacebookLiteCode.repository.ComentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.FacebookLiteCode.repository.UsersRepository;
import com.example.FacebookLiteCode.repository.PostRepository;
import com.example.FacebookLiteCode.dto.CommentRequestDTO;
import com.example.FacebookLiteCode.dto.CommentResponseDTO;
import com.example.FacebookLiteCode.dto.mapper.CommentMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ComentsService {
    
    @Autowired
    private ComentsRepository comentsRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentMapper commentMapper;
    
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

    // DTO-based API
    public List<CommentResponseDTO> getAllCommentsDTO() {
        return comentsRepository.findAll().stream()
                .map(commentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public CommentResponseDTO getCommentResponseById(int id) {
        return comentsRepository.findById(id)
                .map(commentMapper::toResponseDTO)
                .orElse(null);
    }

    public CommentResponseDTO createComment(CommentRequestDTO dto) {
        // Validate character count (70 characters max)
        if (dto.getContent().length() > 70) {
            throw new IllegalArgumentException("Comment exceeds 70 characters limit");
        }
        
        Users user = usersRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + dto.getUserId()));
        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + dto.getPostId()));
        Coments entity = commentMapper.toEntity(dto, user, post);
        Coments saved = comentsRepository.save(entity);
        return commentMapper.toResponseDTO(saved);
    }
    
    private int countWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        return text.trim().split("\\s+").length;
    }

    public Optional<CommentResponseDTO> updateComment(int id, CommentRequestDTO dto) {
        Optional<Coments> existingOpt = comentsRepository.findById(id);
        if (existingOpt.isEmpty()) return Optional.empty();
        Coments existing = existingOpt.get();
        commentMapper.updateEntityFromDTO(dto, existing);
        Coments saved = comentsRepository.save(existing);
        return Optional.of(commentMapper.toResponseDTO(saved));
    }

    public List<CommentResponseDTO> getCommentsByPostIdDTO(int postId) {
        return comentsRepository.findByPostPostId(postId).stream()
                .map(commentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<CommentResponseDTO> getCommentsByUserIdDTO(int userId) {
        return comentsRepository.findByUserUserId(userId).stream()
                .map(commentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<CommentResponseDTO> getCommentsByContentDTO(String content) {
        return comentsRepository.findByContentContainingIgnoreCase(content).stream()
                .map(commentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
