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
    private int usersGroupsId;

    @Column(name = "user_id")
    private int userId;
    
    @Column(name = "group_id")
    private int groupId;
    
    @Column(name = "group_admin")
    private boolean groupAdmin;

    // Relații ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private Users user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", insertable = false, updatable = false)
    private Groups group;
}