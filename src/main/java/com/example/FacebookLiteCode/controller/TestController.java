package com.example.FacebookLiteCode.controller;

import com.example.FacebookLiteCode.model.Users;
import com.example.FacebookLiteCode.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {

    @Autowired
    private UsersService usersService;

    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "OK");
        status.put("message", "FacebookLite API is running");
        return status;
    }

    @GetMapping("/users")
    public List<Users> getAllUsers() {
        return usersService.getAllUsers();
    }

    @GetMapping("/debug")
    public Map<String, Object> debugInfo() {
        Map<String, Object> debug = new HashMap<>();
        debug.put("totalUsers", usersService.getAllUsers().size());
        debug.put("users", usersService.getAllUsers());
        return debug;
    }
}
