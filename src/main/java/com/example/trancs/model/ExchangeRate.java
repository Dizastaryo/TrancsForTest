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
public class ExchangeRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String currencyPair;
    private double closingRate;
    private LocalDate localDate;
    public ExchangeRate(String currencyPair, double closingRate, LocalDate localDate) {
        this.currencyPair = currencyPair;
        this.closingRate = closingRate;
        this.localDate = localDate;
    }
}


