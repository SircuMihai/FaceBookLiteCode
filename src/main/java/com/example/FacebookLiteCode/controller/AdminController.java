package com.example.FacebookLiteCode.controller;

import com.example.FacebookLiteCode.model.Admin;
import com.example.FacebookLiteCode.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admins")
@CrossOrigin(origins = "*")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @GetMapping
    public List<Admin> getAllAdmins() {
        return adminService.getAllAdmins();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Admin> getAdminById(@PathVariable int id) {
        Optional<Admin> admin = adminService.getAdminById(id);
        return admin.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Admin createAdmin(@RequestBody Admin admin) {
        return adminService.saveAdmin(admin);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Admin> updateAdmin(@PathVariable int id, @RequestBody Admin admin) {
        if (adminService.getAdminById(id).isPresent()) {
            admin.setAdmin_id(id);
            return ResponseEntity.ok(adminService.saveAdmin(admin));
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable int id) {
        if (adminService.getAdminById(id).isPresent()) {
            adminService.deleteAdmin(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/name/{name}")
    public ResponseEntity<Admin> getAdminByName(@PathVariable String name) {
        Admin admin = adminService.findByName(name);
        return admin != null ? ResponseEntity.ok(admin) : ResponseEntity.notFound().build();
    }
    
    @PostMapping("/login")
    public ResponseEntity<Admin> loginAdmin(@RequestBody Admin loginData) {
        Admin admin = adminService.findByNameAndPassword(loginData.getName(), loginData.getPassword());
        return admin != null ? ResponseEntity.ok(admin) : ResponseEntity.notFound().build();
    }
}
