package com.example.FacebookLiteCode.services;

import com.example.FacebookLiteCode.model.Users;
import com.example.FacebookLiteCode.repository.UsersRepository;
import com.example.FacebookLiteCode.repository.LikeRepository;
import com.example.FacebookLiteCode.repository.MesagesRepository;
import com.example.FacebookLiteCode.repository.FriendshipUserRepository;
import com.example.FacebookLiteCode.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.FacebookLiteCode.dto.UserRequestDTO;
import com.example.FacebookLiteCode.dto.UpdateUserRequestDTO;
import com.example.FacebookLiteCode.dto.UserResponseDTO;
import com.example.FacebookLiteCode.dto.mapper.UserMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsersService {
    
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private MesagesRepository mesagesRepository;

    @Autowired
    private FriendshipUserRepository friendshipUserRepository;

    @Autowired
    private PostRepository postRepository;
    
    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }
    
    public Optional<Users> getUserById(int id) {
        return usersRepository.findById(id);
    }
    
    public Users saveUser(Users user) {
        return usersRepository.save(user);
    }
    
    @Transactional
    public void deleteUser(int id) {
        Optional<Users> userOpt = usersRepository.findById(id);
        if (userOpt.isEmpty()) {
            return;
        }
        
        // Șterge toate like-urile utilizatorului (like-uri date de el)
        likeRepository.deleteByUserUserId(id);
        
        // Șterge toate like-urile de pe posturile utilizatorului (like-uri primite de el)
        postRepository.findByUserUserId(id).forEach(post -> {
            likeRepository.deleteByPostPostId(post.getPostId());
        });
        
        // Șterge toate mesajele trimise de utilizator
        mesagesRepository.findByUserUserId(id).forEach(mesagesRepository::delete);
        
        // Șterge toate mesajele primite de utilizator
        mesagesRepository.findByReseverUserId(id).forEach(mesagesRepository::delete);
        
        // Șterge toate prieteniile unde utilizatorul este user1 sau user2
        friendshipUserRepository.findByUser1UserId(id).forEach(friendshipUserRepository::delete);
        friendshipUserRepository.findByUser2UserId(id).forEach(friendshipUserRepository::delete);
        
        // Șterge utilizatorul (cascade va șterge automat posturile și comentariile)
        usersRepository.deleteById(id);
    }
    
    public Optional<Users> findByUsername(String username) {
        return usersRepository.findByUsername(username);
    }
    
    public Optional<Users> findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }
    
    public List<Users> findByPrivateAccount(boolean privateAccount) {
        return usersRepository.findByPrivateAccount(privateAccount);
    }
    
    public List<Users> findByFirstNameContaining(String firstName) {
        return usersRepository.findByFirstNameContainingIgnoreCase(firstName);
    }
    
    public List<Users> findByLastNameContaining(String lastName) {
        return usersRepository.findByLastNameContainingIgnoreCase(lastName);
    }

    // DTO-based API
    public List<UserResponseDTO> getAllUsersDTO() {
        return usersRepository.findAll().stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public UserResponseDTO getUserResponseById(int id) {
        return usersRepository.findById(id)
                .map(userMapper::toResponseDTO)
                .orElse(null);
    }

    public UserResponseDTO createUser(UserRequestDTO dto) {
        Users entity = userMapper.toEntity(dto);
        Users saved = usersRepository.save(entity);
        return userMapper.toResponseDTO(saved);
    }

    public Optional<UserResponseDTO> updateUser(int id, UpdateUserRequestDTO dto) {
        Optional<Users> existingOpt = usersRepository.findById(id);
        if (existingOpt.isEmpty()) return Optional.empty();
        Users existing = existingOpt.get();
        userMapper.updateEntityFromUpdateDTO(dto, existing);
        Users saved = usersRepository.save(existing);
        return Optional.of(userMapper.toResponseDTO(saved));
    }

    public List<UserResponseDTO> findByPrivateAccountDTO(boolean privateAccount) {
        return usersRepository.findByPrivateAccount(privateAccount).stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<UserResponseDTO> findByFirstNameContainingDTO(String firstName) {
        return usersRepository.findByFirstNameContainingIgnoreCase(firstName).stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<UserResponseDTO> findByLastNameContainingDTO(String lastName) {
        return usersRepository.findByLastNameContainingIgnoreCase(lastName).stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<UserResponseDTO> findByUsernameDTO(String username) {
        return usersRepository.findByUsername(username).map(userMapper::toResponseDTO);
    }

    public Optional<UserResponseDTO> findByEmailDTO(String email) {
        return usersRepository.findByEmail(email).map(userMapper::toResponseDTO);
    }
}
