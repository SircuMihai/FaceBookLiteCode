package com.example.FacebookLiteCode.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@Table(name = "coments")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Coments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private int comment_id;
    
    @Column(name = "content", nullable = false)
    private String content;
    
    @Column(name = "post_id")
    private int post_id;
    
    @Column(name = "user_id")
    private int user_id;
}