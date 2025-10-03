package com.example.FacebookLiteCode.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@Table(name = "users_groups")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Users_Groups {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_groups_id")
    private int users_groups_id;

    @Column(name = "user_id")
    private int user_id;
    
    @Column(name = "group_id")
    private int group_id;
    
    @Column(name = "group_admin")
    private boolean group_admin;

    // Relații ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private Users user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", insertable = false, updatable = false)
    private Groups group;
}