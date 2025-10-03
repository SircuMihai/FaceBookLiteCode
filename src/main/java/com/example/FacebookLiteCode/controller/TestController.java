package com.example.FacebookLiteCode.controller;

import com.example.FacebookLiteCode.model.Users;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    
    @GetMapping("/test")
    public String test() {
        return "Application is running!";
    }
}
