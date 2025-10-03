package com.example.FacebookLiteCode.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@Table(name = "mesages")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Mesages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private int message_id;
    
    @Column(name = "message", nullable = false)
    private String message;
    
    @Column(name = "data")
    private String data;
    
    @Column(name = "is_pin")
    private boolean is_pin;
    
    @Column(name = "user_id")
    private int user_id;
    
    @Column(name = "group_id")
    private int group_id;
}