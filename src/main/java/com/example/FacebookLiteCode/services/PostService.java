package com.example.FacebookLiteCode.services;

import com.example.FacebookLiteCode.model.Post;
import com.example.FacebookLiteCode.model.Users;
import com.example.FacebookLiteCode.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.FacebookLiteCode.repository.UsersRepository;
import com.example.FacebookLiteCode.dto.PostRequestDTO;
import com.example.FacebookLiteCode.dto.PostResponseDTO;
import com.example.FacebookLiteCode.dto.mapper.PostMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {
    
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PostMapper postMapper;
    
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

    // DTO-based API
    public List<PostResponseDTO> getAllPostsDTO() {
        return postRepository.findAll().stream()
                .map(postMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public PostResponseDTO getPostResponseById(int id) {
        return postRepository.findById(id)
                .map(postMapper::toResponseDTO)
                .orElse(null);
    }

    public PostResponseDTO createPost(PostRequestDTO dto) {
        Users user = usersRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + dto.getUserId()));
        Post entity = postMapper.toEntity(dto, user);
        Post saved = postRepository.save(entity);
        return postMapper.toResponseDTO(saved);
    }

    public Optional<PostResponseDTO> updatePost(int id, PostRequestDTO dto) {
        Optional<Post> existingOpt = postRepository.findById(id);
        if (existingOpt.isEmpty()) return Optional.empty();
        Post existing = existingOpt.get();
        postMapper.updateEntityFromDTO(dto, existing);
        Post saved = postRepository.save(existing);
        return Optional.of(postMapper.toResponseDTO(saved));
    }

    public List<PostResponseDTO> getPostsByUserIdDTO(int userId) {
        return postRepository.findByUserUserId(userId).stream()
                .map(postMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<PostResponseDTO> getPostsByContentDTO(String content) {
        return postRepository.findByContentContainingIgnoreCase(content).stream()
                .map(postMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<PostResponseDTO> getPostsByDateDTO(String date) {
        return postRepository.findByCreatedAtContaining(date).stream()
                .map(postMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
