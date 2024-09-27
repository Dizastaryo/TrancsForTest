package com.example.trancs.repository;
import java.time.LocalDate;
import java.util.Optional;
import com.example.trancs.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    Optional<ExchangeRate> findTopByCurrencyPairAndLocalDateLessThanEqual(String currencyPair, LocalDate localDate);
}


