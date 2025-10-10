package com.example.FacebookLiteCode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserResponseDTO {
    private int userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String profilePicture;
    private String lastLogin;
    private boolean privateAccount;
    private String role;
}
