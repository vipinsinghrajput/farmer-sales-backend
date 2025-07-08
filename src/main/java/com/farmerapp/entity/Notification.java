package com.farmerapp.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "farmer_id", nullable = true)
    private Farmer farmer;
    
    @ManyToOne
    @JoinColumn(name = "consumer_id",nullable = true)
    private Consumer consumer;
    
    @ManyToOne
    @JoinColumn(name = "admin_id",nullable = true)
    private Admin admin;

    private String message;
    
    private Boolean isRead ; 

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
      
    }
}
