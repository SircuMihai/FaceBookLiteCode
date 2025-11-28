package com.example.FacebookLiteCode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    
    private String token; // Access token
    private String refreshToken; // Refresh token
    private String type = "Bearer";
    private int userId;
    private String username;
    private String email;
    private String role;

    public LoginResponseDTO(String token, int userId, String username, String email, String role) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public LoginResponseDTO(String token, String refreshToken, int userId, String username, String email, String role) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
    }
}
