package com.example.FacebookLiteCode.repository;

import com.example.FacebookLiteCode.model.FriendshipUser;
import com.example.FacebookLiteCode.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipUserRepository extends JpaRepository<FriendshipUser, Integer> {
    List<FriendshipUser> findByUser1(Users user1);
    List<FriendshipUser> findByUser2(Users user2);
    List<FriendshipUser> findByUser1UserId(int user1Id);
    List<FriendshipUser> findByUser2UserId(int user2Id);
    List<FriendshipUser> findByStatus(String status);
    Optional<FriendshipUser> findByUser1UserIdAndUser2UserId(int user1Id, int user2Id);
}
