package com.example.FacebookLiteCode.services;

import com.example.FacebookLiteCode.model.UserFriends;
import com.example.FacebookLiteCode.repository.UserFriendsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserFriendsService {
    
    @Autowired
    private UserFriendsRepository userFriendsRepository;
    
    public List<UserFriends> getAllUserFriends() {
        return userFriendsRepository.findAll();
    }
    
    public Optional<UserFriends> getUserFriendsById(int id) {
        return userFriendsRepository.findById(id);
    }
    
    public UserFriends saveUserFriends(UserFriends userFriends) {
        return userFriendsRepository.save(userFriends);
    }
    
    public void deleteUserFriends(int id) {
        userFriendsRepository.deleteById(id);
    }
    
    public List<UserFriends> getUserFriendsByUserId(int userId) {
        return userFriendsRepository.findByUserId(userId);
    }
    
    public List<UserFriends> getUserFriendsByFriendId(int friendId) {
        return userFriendsRepository.findByFriendId(friendId);
    }
    
    public UserFriends getUserFriendsByUserIdAndFriendId(int userId, int friendId) {
        return userFriendsRepository.findByUserIdAndFriendId(userId, friendId);
    }
    
    public List<UserFriends> getUserFriendsByUserIdOrFriendId(int userId, int friendId) {
        return userFriendsRepository.findByUserIdOrFriendId(userId, friendId);
    }
}
