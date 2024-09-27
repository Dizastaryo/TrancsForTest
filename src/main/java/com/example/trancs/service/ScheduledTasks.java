package com.example.trancs.service;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
@EnableScheduling
@Component
public class ScheduledTasks {
    @Autowired
    private ExchangeRateService exchangeRateService;
    @Scheduled(cron = "0 0 0 * * ?")
    public void fetchAndSaveExchangeRates() {
        exchangeRateService.fetchExchangeRates();
    }
}


