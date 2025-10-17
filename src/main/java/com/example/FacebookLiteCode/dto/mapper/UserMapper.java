package com.example.FacebookLiteCode.dto.mapper;

import com.example.FacebookLiteCode.dto.UserRequestDTO;
import com.example.FacebookLiteCode.dto.UpdateUserRequestDTO;
import com.example.FacebookLiteCode.dto.UserResponseDTO;
import com.example.FacebookLiteCode.model.Users;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    
    // Convert Entity to Response DTO
    public UserResponseDTO toResponseDTO(Users user) {
        if (user == null) {
            return null;
        }
        
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        // Password is intentionally excluded
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setLastLogin(user.getLastLogin());
        dto.setPrivateAccount(user.isPrivateAccount());
        dto.setRole(user.getRole());
        
        return dto;
    }
    
    // Convert Request DTO to Entity
    public Users toEntity(UserRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Users user = new Users();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword()); // Should be encrypted in service layer
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setProfilePicture(dto.getProfilePicture());
        user.setPrivateAccount(dto.isPrivateAccount());
        user.setRole(dto.getRole());
        
        return user;
    }
    
    // Update existing entity from Request DTO
    public void updateEntityFromDTO(UserRequestDTO dto, Users user) {
        if (dto == null || user == null) {
            return;
        }
        
        if (dto.getUsername() != null) {
            user.setUsername(dto.getUsername());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(dto.getPassword()); // Should be encrypted in service layer
        }
        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }
        if (dto.getProfilePicture() != null) {
            user.setProfilePicture(dto.getProfilePicture());
        }
        user.setPrivateAccount(dto.isPrivateAccount());
        if (dto.getRole() != null) {
            user.setRole(dto.getRole());
        }
    }
    
    // Update existing entity from Update Request DTO (password optional)
    public void updateEntityFromUpdateDTO(UpdateUserRequestDTO dto, Users user) {
        if (dto == null || user == null) {
            return;
        }
        
        if (dto.getUsername() != null) {
            user.setUsername(dto.getUsername());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(dto.getPassword()); // Should be encrypted in service layer
        }
        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }
        if (dto.getProfilePicture() != null) {
            user.setProfilePicture(dto.getProfilePicture());
        }
        if (dto.getPrivateAccount() != null) {
            user.setPrivateAccount(dto.getPrivateAccount());
        }
        if (dto.getRole() != null) {
            user.setRole(dto.getRole());
        }
    }
}
