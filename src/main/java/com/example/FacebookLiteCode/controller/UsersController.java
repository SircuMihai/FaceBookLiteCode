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

    @GetMapping
    public List<Users> getAllUsers() {
        return usersService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Users> getUserById(@PathVariable int id) {
        Optional<Users> user = usersService.getUserById(id);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Users createUser(@RequestBody Users user) {
        return usersService.saveUser(user);
    }

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
