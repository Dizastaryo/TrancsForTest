package com.example.trancs.model;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDate date;
    @Column(nullable = false)
    private Double amount;
    @Column(nullable = false)
    private String operation;
    @Column
    private String details;
    @Column(nullable = false)
    private String currency;
    @Column(name = "limit_exceeded", nullable = false)
    private Boolean limitExceeded = false;
}


