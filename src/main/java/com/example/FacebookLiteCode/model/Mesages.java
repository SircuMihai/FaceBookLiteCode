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
    private int messageId;
    
    @Column(name = "message", nullable = false)
    private String message;
    
    @Column(name = "data")
    private String data;
    
    @Column(name = "is_pin")
    private boolean isPin;
    
    // Relații
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "resever_id", nullable = false)
    private Users resever;
    

}