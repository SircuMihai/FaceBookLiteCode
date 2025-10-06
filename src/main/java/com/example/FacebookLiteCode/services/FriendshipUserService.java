package com.example.FacebookLiteCode.services;

import com.example.FacebookLiteCode.model.FriendshipUser;
import com.example.FacebookLiteCode.model.Users;
import com.example.FacebookLiteCode.repository.FriendshipUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FriendshipUserService {

    @Autowired
    private FriendshipUserRepository friendshipUserRepository;

    public List<FriendshipUser> getAllFriendships() {
        return friendshipUserRepository.findAll();
    }

    public Optional<FriendshipUser> getFriendshipById(int id) {
        return friendshipUserRepository.findById(id);
    }

    public FriendshipUser saveFriendship(FriendshipUser friendship) {
        return friendshipUserRepository.save(friendship);
    }

    public void deleteFriendship(int id) {
        friendshipUserRepository.deleteById(id);
    }

    public List<FriendshipUser> getFriendshipsByUser1(Users user) {
        return friendshipUserRepository.findByUser1(user);
    }

    public List<FriendshipUser> getFriendshipsByUser2(Users user) {
        return friendshipUserRepository.findByUser2(user);
    }

    public List<FriendshipUser> getFriendshipsByUser1Id(int user1Id) {
        return friendshipUserRepository.findByUser1UserId(user1Id);
    }

    public List<FriendshipUser> getFriendshipsByUser2Id(int user2Id) {
        return friendshipUserRepository.findByUser2UserId(user2Id);
    }

    public List<FriendshipUser> getFriendshipsByStatus(String status) {
        return friendshipUserRepository.findByStatus(status);
    }

    public Optional<FriendshipUser> getFriendshipBetweenUsers(int user1Id, int user2Id) {
        return friendshipUserRepository.findByUser1UserIdAndUser2UserId(user1Id, user2Id);
    }
}
