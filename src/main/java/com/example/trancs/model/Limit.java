package com.example.trancs.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "limits")
public class Limit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Double amount;
    @Column(name = "currency", nullable = false)
    private String currency = "USD";
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    public Limit(Double amount, LocalDateTime createdAt) {
        this.amount = amount;
        this.createdAt = createdAt;
    }
}



