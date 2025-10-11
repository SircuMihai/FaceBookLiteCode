package com.example.FacebookLiteCode.services;

import com.example.FacebookLiteCode.model.FriendshipUser;
import com.example.FacebookLiteCode.model.Users;
import com.example.FacebookLiteCode.repository.FriendshipUserRepository;
import com.example.FacebookLiteCode.repository.UsersRepository;
import com.example.FacebookLiteCode.dto.FriendshipRequestDTO;
import com.example.FacebookLiteCode.dto.FriendshipResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FriendshipUserService {

    @Autowired
    private FriendshipUserRepository friendshipUserRepository;

    @Autowired
    private UsersRepository usersRepository;

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

    // DTO-based API
    public FriendshipResponseDTO createFriendship(FriendshipRequestDTO dto) {
        Users user1 = usersRepository.findById(dto.getUser1Id())
                .orElseThrow(() -> new IllegalArgumentException("User1 not found: " + dto.getUser1Id()));
        Users user2 = usersRepository.findById(dto.getUser2Id())
                .orElseThrow(() -> new IllegalArgumentException("User2 not found: " + dto.getUser2Id()));

        FriendshipUser entity = new FriendshipUser();
        entity.setUser1(user1);
        entity.setUser2(user2);
        entity.setStatus(dto.getStatus());

        FriendshipUser saved = friendshipUserRepository.save(entity);
        return toResponseDTO(saved);
    }

    public FriendshipResponseDTO toResponseDTO(FriendshipUser f) {
        FriendshipResponseDTO res = new FriendshipResponseDTO();
        res.setFriendshipId(f.getFrienshipId());
        if (f.getUser1() != null) res.setUser1Id(f.getUser1().getUserId());
        if (f.getUser2() != null) res.setUser2Id(f.getUser2().getUserId());
        res.setStatus(f.getStatus());
        return res;
    }
}

