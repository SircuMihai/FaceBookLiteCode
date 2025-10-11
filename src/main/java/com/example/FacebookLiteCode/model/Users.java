package com.example.FacebookLiteCode.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;
    
    @Column(name = "username", nullable = false, unique = true)
    private String username;
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column(name = "password", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "profile_picture")
    private String profilePicture;
    
    @Column(name = "last_login")
    private String lastLogin;
    
    @Column(name = "private_account")
    private boolean privateAccount;

    @Column(name = "role")
    private String role;

    // Relații OneToMany
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Coments> comments;

    // Relații pentru prietenii (user1 și user2)
    @OneToMany(mappedBy = "user1", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FriendshipUser> friendshipsAsUser1;

    @OneToMany(mappedBy = "user2", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FriendshipUser> friendshipsAsUser2;

    // Relații pentru mesaje trimise și primite
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Mesages> sentMessages;

    @OneToMany(mappedBy = "resever", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Mesages> receivedMessages;
}