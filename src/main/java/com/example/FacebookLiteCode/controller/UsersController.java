package com.example.FacebookLiteCode.controller;

import com.example.FacebookLiteCode.model.Users;
import com.example.FacebookLiteCode.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UsersController {

    @Autowired
    private UsersService usersService;

//    @GetMapping
//    public ApiResponseDto<List<UserResponseDto>> getAllUsers() {
//        try {
//            List<Users> users = usersService.getAllUsers();
//            List<UserResponseDto> userDtos = mapperService.toUserDtoList(users).stream()
//                    .map(userDto -> {
//                        // Convertim UserDto la UserResponseDto
//                        UserResponseDto responseDto = new UserResponseDto();
//                        responseDto.setUserId(userDto.getUserId());
//                        responseDto.setUsername(userDto.getUsername());
//                        responseDto.setEmail(userDto.getEmail());
//                        responseDto.setFirstName(userDto.getFirstName());
//                        responseDto.setLastName(userDto.getLastName());
//                        responseDto.setProfilePicture(userDto.getProfilePicture());
//                        responseDto.setLastLogin(userDto.getLastLogin());
//                        responseDto.setPrivateAccount(userDto.isPrivateAccount());
//                        return responseDto;
//                    })
//                    .collect(java.util.stream.Collectors.toList());
//            return ApiResponseDto.success(userDtos, "Utilizatori găsiți cu succes");
//        } catch (Exception e) {
//            return ApiResponseDto.error("Eroare la preluarea utilizatorilor: " + e.getMessage());
//        }
//    }
//
//    @GetMapping("/{id}")
//    public ApiResponseDto<UserResponseDto> getUserById(@PathVariable int id) {
//        try {
//            Optional<Users> user = usersService.getUserById(id);
//            if (user.isPresent()) {
//                UserResponseDto userResponseDto = mapperService.toUserResponseDto(user.get());
//                return ApiResponseDto.success(userResponseDto, "Utilizator găsit cu succes");
//            } else {
//                return ApiResponseDto.error("Utilizatorul cu ID-ul " + id + " nu a fost găsit");
//            }
//        } catch (Exception e) {
//            return ApiResponseDto.error("Eroare la preluarea utilizatorului: " + e.getMessage());
//        }
//    }
//
//    @PostMapping
//    public ApiResponseDto<UserResponseDto> createUser(@RequestBody UserRequestDto userRequestDto) {
//        try {
//            // Validări de bază
//            if (userRequestDto.getUsername() == null || userRequestDto.getUsername().trim().isEmpty()) {
//                return ApiResponseDto.error("Username-ul este obligatoriu");
//            }
//            if (userRequestDto.getEmail() == null || userRequestDto.getEmail().trim().isEmpty()) {
//                return ApiResponseDto.error("Email-ul este obligatoriu");
//            }
//            if (userRequestDto.getPassword() == null || userRequestDto.getPassword().trim().isEmpty()) {
//                return ApiResponseDto.error("Parola este obligatorie");
//            }
//
//            // Convertim DTO-ul la entitate
//            Users user = mapperService.toUserEntity(userRequestDto);
//
//            // Salvăm utilizatorul
//            Users savedUser = usersService.saveUser(user);
//
//            // Convertim înapoi la DTO pentru răspuns
//            UserResponseDto userResponseDto = mapperService.toUserResponseDto(savedUser);
//
//            return ApiResponseDto.success(userResponseDto, "Utilizator creat cu succes");
//        } catch (Exception e) {
//            return ApiResponseDto.error("Eroare la crearea utilizatorului: " + e.getMessage());
//        }
//    }

    @PutMapping("/{id}")
    public ResponseEntity<Users> updateUser(@PathVariable int id, @RequestBody Users user) {
        if (usersService.getUserById(id).isPresent()) {
            user.setUserId(id);
            return ResponseEntity.ok(usersService.saveUser(user));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        if (usersService.getUserById(id).isPresent()) {
            usersService.deleteUser(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<Users> getUserByUsername(@PathVariable String username) {
        Optional<Users> user = usersService.findByUsername(username);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Users> getUserByEmail(@PathVariable String email) {
        Optional<Users> user = usersService.findByEmail(email);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/private/{privateAccount}")
    public List<Users> getUsersByPrivateAccount(@PathVariable boolean privateAccount) {
        return usersService.findByPrivateAccount(privateAccount);
    }

    @GetMapping("/search/firstname/{firstName}")
    public List<Users> searchUsersByFirstName(@PathVariable String firstName) {
        return usersService.findByFirstNameContaining(firstName);
    }

    @GetMapping("/search/lastname/{lastName}")
    public List<Users> searchUsersByLastName(@PathVariable String lastName) {
        return usersService.findByLastNameContaining(lastName);
    }

}

//    // Endpoint pentru autentificare
//    @PostMapping("/login")
//    public ApiResponseDto<AuthResponseDto> login(@RequestBody LoginDto loginDto) {
//        try {
//            if (loginDto.getUsername() == null || loginDto.getPassword() == null) {
//                return ApiResponseDto.error("Username și parola sunt obligatorii");
//            }
//
//            Optional<Users> user = usersService.findByUsername(loginDto.getUsername());
//            if (user.isPresent() && user.get().getPassword().equals(loginDto.getPassword())) {
//                UserResponseDto userResponseDto = mapperService.toUserResponseDto(user.get());
//
//                AuthResponseDto authResponse = new AuthResponseDto();
//                authResponse.setSuccess(true);
//                authResponse.setMessage("Autentificare reușită");
//                authResponse.setUser(userResponseDto);
//                authResponse.setToken("jwt-token-here"); // Aici vei integra JWT
//
//                return ApiResponseDto.success(authResponse, "Autentificare reușită");
//            } else {
//                return ApiResponseDto.error("Username sau parolă incorecte");
//            }
//        } catch (Exception e) {
//            return ApiResponseDto.error("Eroare la autentificare: " + e.getMessage());
//        }
//    }
//
//    // Endpoint pentru actualizarea profilului
//    @PutMapping("/{id}/profile")
//    public ApiResponseDto<UserResponseDto> updateProfile(@PathVariable int id, @RequestBody UserRequestDto userRequestDto) {
//        try {
//            Optional<Users> existingUser = usersService.getUserById(id);
//            if (existingUser.isPresent()) {
//                Users user = existingUser.get();
//
//                // Actualizăm doar câmpurile furnizate
//                if (userRequestDto.getFirstName() != null) {
//                    user.setFirstName(userRequestDto.getFirstName());
//                }
//                if (userRequestDto.getLastName() != null) {
//                    user.setLastName(userRequestDto.getLastName());
//                }
//                if (userRequestDto.getProfilePicture() != null) {
//                    user.setProfilePicture(userRequestDto.getProfilePicture());
//                }
//                if (userRequestDto.getEmail() != null) {
//                    user.setEmail(userRequestDto.getEmail());
//                }
//                user.setPrivateAccount(userRequestDto.isPrivateAccount());
//
//                Users updatedUser = usersService.saveUser(user);
//                UserResponseDto userResponseDto = mapperService.toUserResponseDto(updatedUser);
//
//                return ApiResponseDto.success(userResponseDto, "Profil actualizat cu succes");
//            } else {
//                return ApiResponseDto.error("Utilizatorul nu a fost găsit");
//            }
//        } catch (Exception e) {
//            return ApiResponseDto.error("Eroare la actualizarea profilului: " + e.getMessage());
//        }
//    }
//}
