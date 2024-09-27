package com.example.trancs.controller;
import com.example.trancs.model.ExchangeRate;
import com.example.trancs.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.http.ResponseEntity;
    @RestController
    @RequestMapping("/api/exchange-rates")
    @CrossOrigin(origins = "http://localhost:3000")
    public class ExchangeRateController {
        @Autowired
        private ExchangeRateService exchangeRateService;
        @GetMapping("/current")
        public ResponseEntity<List<ExchangeRate>> getCurrentExchangeRates() {
            List<ExchangeRate> rates = exchangeRateService.getCurrentExchangeRates();
            return ResponseEntity.ok(rates);
        }
    }


