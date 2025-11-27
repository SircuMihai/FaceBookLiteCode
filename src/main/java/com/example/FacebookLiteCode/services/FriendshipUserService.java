package com.example.FacebookLiteCode.services;

import com.example.FacebookLiteCode.model.FriendshipUser;
import com.example.FacebookLiteCode.model.Users;
import com.example.FacebookLiteCode.repository.FriendshipUserRepository;
import com.example.FacebookLiteCode.repository.UsersRepository;
import com.example.FacebookLiteCode.dto.FriendshipRequestDTO;
import com.example.FacebookLiteCode.dto.FriendshipResponseDTO;
import com.example.FacebookLiteCode.dto.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FriendshipUserService {

    @Autowired
    private FriendshipUserRepository friendshipUserRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UserMapper userMapper;

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

        // Check if friendship already exists (both directions)
        Optional<FriendshipUser> existingFriendship = friendshipUserRepository.findByUser1UserIdAndUser2UserId(dto.getUser1Id(), dto.getUser2Id());
        if (existingFriendship.isPresent()) {
            throw new IllegalArgumentException("Friendship already exists between these users");
        }
        
        Optional<FriendshipUser> reverseFriendship = friendshipUserRepository.findByUser1UserIdAndUser2UserId(dto.getUser2Id(), dto.getUser1Id());
        if (reverseFriendship.isPresent()) {
            throw new IllegalArgumentException("Friendship already exists between these users");
        }

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
        if (f.getUser1() != null) {
            res.setUser1Id(f.getUser1().getUserId());
            res.setUser1(userMapper.toResponseDTO(f.getUser1()));
        }
        if (f.getUser2() != null) {
            res.setUser2Id(f.getUser2().getUserId());
            res.setUser2(userMapper.toResponseDTO(f.getUser2()));
        }
        res.setStatus(f.getStatus());
        return res;
    }

    public Optional<FriendshipResponseDTO> updateFriendship(int id, FriendshipRequestDTO dto) {
        Optional<FriendshipUser> existingOpt = friendshipUserRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return Optional.empty();
        }

        FriendshipUser existing = existingOpt.get();

        if (dto.getUser1Id() != null) {
            Users user1 = usersRepository.findById(dto.getUser1Id())
                    .orElseThrow(() -> new IllegalArgumentException("User1 not found: " + dto.getUser1Id()));
            existing.setUser1(user1);
        }

        if (dto.getUser2Id() != null) {
            Users user2 = usersRepository.findById(dto.getUser2Id())
                    .orElseThrow(() -> new IllegalArgumentException("User2 not found: " + dto.getUser2Id()));
            existing.setUser2(user2);
        }

        if (dto.getStatus() != null) {
            existing.setStatus(dto.getStatus());
        }

        FriendshipUser saved = friendshipUserRepository.save(existing);
        return Optional.of(toResponseDTO(saved));
    }

    public List<FriendshipResponseDTO> getAllFriendshipsDTO() {
        return friendshipUserRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public FriendshipResponseDTO getFriendshipResponseById(int id) {
        return friendshipUserRepository.findById(id)
                .map(this::toResponseDTO)
                .orElse(null);
    }

    public List<FriendshipResponseDTO> getFriendshipsByUser1IdDTO(int user1Id) {
        return friendshipUserRepository.findByUser1UserId(user1Id).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<FriendshipResponseDTO> getFriendshipsByUser2IdDTO(int user2Id) {
        return friendshipUserRepository.findByUser2UserId(user2Id).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<FriendshipResponseDTO> getFriendshipsByStatusDTO(String status) {
        return friendshipUserRepository.findByStatus(status).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<FriendshipResponseDTO> getFriendshipBetweenUsersDTO(int user1Id, int user2Id) {
        return friendshipUserRepository.findByUser1UserIdAndUser2UserId(user1Id, user2Id)
                .map(this::toResponseDTO);
    }

    // Friend request methods
    public List<FriendshipResponseDTO> getFriendRequestsDTO(int userId) {
        return friendshipUserRepository.findByUser2UserIdAndStatus(userId, "pending").stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<FriendshipResponseDTO> getFriendsDTO(int userId) {
        List<FriendshipUser> user1Friends = friendshipUserRepository.findByUser1UserIdAndStatus(userId, "accepted");
        List<FriendshipUser> user2Friends = friendshipUserRepository.findByUser2UserIdAndStatus(userId, "accepted");
        
        List<FriendshipUser> allFriends = new java.util.ArrayList<>();
        allFriends.addAll(user1Friends);
        allFriends.addAll(user2Friends);
        
        return allFriends.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<FriendshipResponseDTO> acceptFriendship(int friendshipId) {
        Optional<FriendshipUser> friendshipOpt = friendshipUserRepository.findById(friendshipId);
        if (friendshipOpt.isPresent()) {
            FriendshipUser friendship = friendshipOpt.get();
            
            // Check if friendship is in pending status
            if (!"pending".equalsIgnoreCase(friendship.getStatus())) {
                if ("accepted".equalsIgnoreCase(friendship.getStatus())) {
                    throw new IllegalArgumentException("Friend request has already been accepted");
                } else {
                    throw new IllegalArgumentException("Friend request cannot be accepted. Current status: " + friendship.getStatus());
                }
            }
            
            friendship.setStatus("accepted");
            FriendshipUser saved = friendshipUserRepository.save(friendship);
            return Optional.of(toResponseDTO(saved));
        }
        return Optional.empty();
    }
}

