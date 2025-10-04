package com.example.FacebookLiteCode.services;

import com.example.FacebookLiteCode.model.Users;
import com.example.FacebookLiteCode.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsersService {
    
    @Autowired
    private UsersRepository usersRepository;
    
    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }
    
    public Optional<Users> getUserById(int id) {
        return usersRepository.findById(id);
    }
    
    public Users saveUser(Users user) {
        return usersRepository.save(user);
    }
    
    public void deleteUser(int id) {
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
}
