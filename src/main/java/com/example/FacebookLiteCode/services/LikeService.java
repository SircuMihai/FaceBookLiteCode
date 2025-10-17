package com.example.FacebookLiteCode.services;

import com.example.FacebookLiteCode.model.Like;
import com.example.FacebookLiteCode.model.Post;
import com.example.FacebookLiteCode.model.Users;
import com.example.FacebookLiteCode.repository.LikeRepository;
import com.example.FacebookLiteCode.repository.PostRepository;
import com.example.FacebookLiteCode.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class LikeService {
    
    @Autowired
    private LikeRepository likeRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UsersRepository usersRepository;
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public boolean toggleLike(int postId, int userId) {
        // Check if user already liked this post
        if (likeRepository.existsByUserUserIdAndPostPostId(userId, postId)) {
            // Unlike the post
            likeRepository.findByUserUserIdAndPostPostId(userId, postId).ifPresent(likeRepository::delete);
            updatePostLikeCount(postId);
            return false; // Post was unliked
        } else {
            // Like the post
            Post post = postRepository.findById(postId).orElse(null);
            Users user = usersRepository.findById(userId).orElse(null);
            
            if (post != null && user != null) {
                Like like = new Like();
                like.setUser(user);
                like.setPost(post);
                like.setCreatedAt(LocalDateTime.now().format(formatter));
                likeRepository.save(like);
                updatePostLikeCount(postId);
                return true; // Post was liked
            }
            return false;
        }
    }
    
    public boolean hasUserLiked(int postId, int userId) {
        return likeRepository.existsByUserUserIdAndPostPostId(userId, postId);
    }
    
    public long getLikeCount(int postId) {
        return likeRepository.countByPostPostId(postId);
    }
    
    private void updatePostLikeCount(int postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post != null) {
            long likeCount = likeRepository.countByPostPostId(postId);
            post.setLikesCount((int) likeCount);
            postRepository.save(post);
        }
    }
}
