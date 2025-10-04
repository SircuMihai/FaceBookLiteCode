package com.example.FacebookLiteCode.repository;

import com.example.FacebookLiteCode.model.Coments;
import com.example.FacebookLiteCode.model.Post;
import com.example.FacebookLiteCode.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentsRepository extends JpaRepository<Coments, Integer> {
    List<Coments> findByPost(Post post);
    List<Coments> findByUser(Users user);
    List<Coments> findByPostPostId(int postId);
    List<Coments> findByUserUserId(int userId);
    List<Coments> findByContentContainingIgnoreCase(String content);
}
