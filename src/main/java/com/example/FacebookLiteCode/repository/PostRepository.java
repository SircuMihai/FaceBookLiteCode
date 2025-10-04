package com.example.FacebookLiteCode.repository;

import com.example.FacebookLiteCode.model.Post;
import com.example.FacebookLiteCode.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findByUser(Users user);
    List<Post> findByUserUserId(int userId);
    List<Post> findByContentContainingIgnoreCase(String content);
    List<Post> findByCreatedAtContaining(String date);
}
