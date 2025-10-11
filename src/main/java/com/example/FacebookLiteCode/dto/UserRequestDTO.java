package com.example.FacebookLiteCode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonAlias;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRequestDTO {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @JsonAlias({"username"})
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @JsonAlias({"email"})
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @JsonAlias({"password"})
    private String password;
    
    @JsonAlias({"first_name", "firstName"})
    private String firstName;

    @JsonAlias({"last_name", "lastName"})
    private String lastName;

    @JsonAlias({"profile_picture", "profilePicture"})
    private String profilePicture;

    @JsonAlias({"private_account", "privateAccount"})
    private boolean privateAccount;

    @JsonAlias({"role"})
    private String role;
}
