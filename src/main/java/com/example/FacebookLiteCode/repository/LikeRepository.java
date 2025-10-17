package com.example.FacebookLiteCode.repository;

import com.example.FacebookLiteCode.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Integer> {
    Optional<Like> findByUserUserIdAndPostPostId(int userId, int postId);
    boolean existsByUserUserIdAndPostPostId(int userId, int postId);
    long countByPostPostId(int postId);
}
