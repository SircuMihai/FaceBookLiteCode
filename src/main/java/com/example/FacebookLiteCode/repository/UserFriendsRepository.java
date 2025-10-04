package com.example.FacebookLiteCode.repository;

import com.example.FacebookLiteCode.model.UserFriends;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFriendsRepository extends JpaRepository<UserFriends, Integer> {
    List<UserFriends> findByUserId(int userId);
    List<UserFriends> findByFriendId(int friendId);
    UserFriends findByUserIdAndFriendId(int userId, int friendId);
    List<UserFriends> findByUserIdOrFriendId(int userId, int friendId);
}
