package com.example.FacebookLiteCode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String profilePicture;
    private boolean privateAccount;
}
