package com.example.FacebookLiteCode.services;

import com.example.FacebookLiteCode.model.Admin;
import com.example.FacebookLiteCode.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    
    @Autowired
    private AdminRepository adminRepository;
    
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }
    
    public Optional<Admin> getAdminById(int id) {
        return adminRepository.findById(id);
    }
    
    public Admin saveAdmin(Admin admin) {
        return adminRepository.save(admin);
    }
    
    public void deleteAdmin(int id) {
        adminRepository.deleteById(id);
    }
    
    public Admin findByName(String name) {
        return adminRepository.findByName(name);
    }
    
    public Admin findByNameAndPassword(String name, String password) {
        return adminRepository.findByNameAndPassword(name, password);
    }
}
