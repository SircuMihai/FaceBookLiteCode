package com.example.FacebookLiteCode.controller;

import com.example.FacebookLiteCode.dto.LoginRequestDTO;
import com.example.FacebookLiteCode.dto.LoginResponseDTO;
import com.example.FacebookLiteCode.dto.RegisterRequestDTO;
import com.example.FacebookLiteCode.dto.RegisterResponseDTO;
import com.example.FacebookLiteCode.model.Users;
import com.example.FacebookLiteCode.repository.UsersRepository;
import com.example.FacebookLiteCode.security.JwtTokenStore;
import com.example.FacebookLiteCode.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtTokenStore jwtTokenStore;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            // Validate input
            if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Username is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            
            if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Password is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            // Check if user exists
            Users user = usersRepository.findByUsername(loginRequest.getUsername().trim())
                    .orElse(null);
            
            if (user == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid username or password. Please check your credentials and try again.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            // Attempt authentication
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername().trim(),
                            loginRequest.getPassword()
                    )
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername().trim());

            // Renew session: revoke all previous tokens for this user before issuing a new one
            jwtTokenStore.revokeTokensByUsername(user.getUsername());

            String token = jwtUtil.generateToken(userDetails);
            jwtTokenStore.storeToken(token, user.getUserId(), user.getUsername());

            LoginResponseDTO response = new LoginResponseDTO(
                    token,
                    user.getUserId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole() != null ? user.getRole() : "USER"
            );

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid username or password. Please check your credentials and try again.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (org.springframework.security.core.userdetails.UsernameNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "User not found. Please check your username and try again.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            String errorMessage = "Login failed. Please try again.";
            if (e.getMessage() != null && !e.getMessage().isEmpty()) {
                errorMessage = "Login failed: " + e.getMessage();
            }
            error.put("error", errorMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        try {
            // Normalize and trim input
            String username = registerRequest.getUsername() != null ? 
                registerRequest.getUsername().trim() : null;
            String email = registerRequest.getEmail() != null ? 
                registerRequest.getEmail().trim().toLowerCase() : null;
            String password = registerRequest.getPassword();
            
            // Validate normalized inputs
            if (username == null || username.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Username is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            
            if (username.length() < 3) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Username must be at least 3 characters long");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            
            if (username.length() > 50) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Username must be no more than 50 characters long");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            
            if (email == null || email.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Email is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            
            // Basic email format validation
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Please enter a valid email address");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            
            if (password == null || password.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Password is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            
            if (password.length() < 6) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Password must be at least 6 characters long");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            
            // Check if username already exists (case-sensitive for usernames)
            if (usersRepository.findByUsername(username).isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Username already exists. Please choose a different username.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            // Check if email already exists (case-insensitive)
            // First try exact match, then case-insensitive match
            if (usersRepository.findByEmail(email).isPresent() || 
                usersRepository.findByEmailIgnoreCase(email).isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "An account with this email already exists. Please use a different email or try logging in.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            // Create new user
            Users newUser = new Users();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPassword(passwordEncoder.encode(password));
            newUser.setFirstName(registerRequest.getFirstName() != null ? 
                registerRequest.getFirstName().trim() : null);
            newUser.setLastName(registerRequest.getLastName() != null ? 
                registerRequest.getLastName().trim() : null);
            newUser.setRole("USER");
            newUser.setPrivateAccount(false);

            Users savedUser = usersRepository.save(newUser);

            RegisterResponseDTO response = new RegisterResponseDTO(
                    savedUser.getUserId(),
                    savedUser.getUsername(),
                    savedUser.getEmail(),
                    savedUser.getRole(),
                    "User registered successfully. Please log in to continue."
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            Map<String, String> error = new HashMap<>();
            if (e.getMessage().contains("username") || e.getMessage().contains("user_name")) {
                error.put("error", "Username already exists. Please choose a different username.");
            } else if (e.getMessage().contains("email")) {
                error.put("error", "An account with this email already exists. Please use a different email or try logging in.");
            } else {
                error.put("error", "Registration failed due to a data conflict. Please check your information and try again.");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            String errorMessage = "Registration failed. Please try again.";
            if (e.getMessage() != null && !e.getMessage().isEmpty()) {
                errorMessage = "Registration failed: " + e.getMessage();
            }
            error.put("error", errorMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String username = jwtUtil.extractUsername(token);
                
                Users user = usersRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                if (jwtTokenStore.isTokenActive(token, username) && jwtUtil.validateToken(token, username)) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("valid", true);
                    response.put("username", username);
                    response.put("userId", user.getUserId());
                    return ResponseEntity.ok(response);
                }
            }
            
            Map<String, Boolean> response = new HashMap<>();
            response.put("valid", false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Token validation failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            jwtTokenStore.revokeToken(token);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User logged out successfully.");
            return ResponseEntity.ok(response);
        }

        Map<String, String> error = new HashMap<>();
        error.put("error", "Authorization header missing or invalid.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
