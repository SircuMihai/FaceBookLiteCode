package com.example.FacebookLiteCode.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="FriendshipUser")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FriendshipUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FriendshipId")
    private int frienshipId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user1Id", nullable=false)
    private Users user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2Id", nullable=false)
    private Users user2;

    @Column(name = "Status")
    private String status;
}
