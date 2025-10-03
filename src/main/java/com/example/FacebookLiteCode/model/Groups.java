package com.example.FacebookLiteCode.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;


@Entity
@Table(name = "groups")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Groups {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private int group_id;
    
    @Column(name = "group_name", nullable = false)
    private String group_name;
    
    @Column(name = "privacy")
    private String privacy;

    // Relații - comentate temporar pentru a evita dependențele circulare
    // @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private List<Users_Groups> userGroups;
    
    // @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private List<Mesages> messages;
}