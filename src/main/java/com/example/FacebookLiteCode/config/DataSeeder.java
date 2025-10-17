package com.example.FacebookLiteCode.config;

import com.example.FacebookLiteCode.model.Post;
import com.example.FacebookLiteCode.model.Users;
import com.example.FacebookLiteCode.services.PostService;
import com.example.FacebookLiteCode.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UsersService usersService;

    @Autowired
    private PostService postService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("DataSeeder starting...");
        // Check if users already exist
        if (usersService.getAllUsers().isEmpty()) {
            System.out.println("No users found, creating sample data...");
            createSampleData();
            System.out.println("Sample data created successfully!");
        } else {
            System.out.println("Users already exist, skipping data seeding.");
        }
    }

    private void createSampleData() {
        // Create sample users
        Users user1 = new Users();
        user1.setUsername("john_doe");
        user1.setEmail("john@example.com");
        user1.setPassword("password123");
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setRole("USER");
        user1.setPrivateAccount(false);
        user1.setLastLogin(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        usersService.saveUser(user1);

        Users user2 = new Users();
        user2.setUsername("jane_smith");
        user2.setEmail("jane@example.com");
        user2.setPassword("password123");
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setRole("USER");
        user2.setPrivateAccount(false);
        user2.setLastLogin(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        usersService.saveUser(user2);

        // Create sample posts
        Post post1 = new Post();
        post1.setContent("Hello world! This is my first post.");
        post1.setUser(user1);
        post1.setCreatedAt(LocalDateTime.now().minusHours(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        postService.savePost(post1);

        Post post2 = new Post();
        post2.setContent("Another example post from Jane.");
        post2.setUser(user2);
        post2.setCreatedAt(LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        postService.savePost(post2);
    }
}
