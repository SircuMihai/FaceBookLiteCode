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
        try {
            System.out.println("DEBUG - Getting all posts...");
            List<Post> posts = postRepository.findAll();
            System.out.println("DEBUG - Found " + posts.size() + " posts");
            
            List<PostResponseDTO> dtos = posts.stream()
                    .map(post -> {
                        try {
                            System.out.println("DEBUG - Mapping post " + post.getPostId());
                            return postMapper.toResponseDTO(post);
                        } catch (Exception e) {
                            System.err.println("Error mapping post " + post.getPostId() + ": " + e.getMessage());
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .filter(dto -> dto != null)
                    .collect(Collectors.toList());
            
            System.out.println("DEBUG - Successfully mapped " + dtos.size() + " DTOs");
            return dtos;
        } catch (Exception e) {
            System.err.println("Error in getAllPostsDTO: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
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
    
    public PostResponseDTO likePost(int postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));
        int currentLikes = post.getLikesCount() != null ? post.getLikesCount() : 0;
        post.setLikesCount(currentLikes + 1);
        Post saved = postRepository.save(post);
        return postMapper.toResponseDTO(saved);
    }
    
    public PostResponseDTO unlikePost(int postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));
        int currentLikes = post.getLikesCount() != null ? post.getLikesCount() : 0;
        if (currentLikes > 0) {
            post.setLikesCount(currentLikes - 1);
        }
        Post saved = postRepository.save(post);
        return postMapper.toResponseDTO(saved);
    }
}
