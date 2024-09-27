package com.example.trancs.service;
import com.example.trancs.model.ExchangeRate;
import com.example.trancs.model.RateResponse;
import com.example.trancs.repository.ExchangeRateRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class ExchangeRateService {
    private final ExchangeRateRepository exchangeRateRepository;
    private final List<String> supportedCurrencies = List.of("USD", "KZT", "RUB");
    private final String EXCHANGE_RATE_API = "https://v6.exchangerate-api.com/v6/a307fbf02ec0ea02e73085a2/latest/USD";
    public ExchangeRateService(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }
    public void fetchExchangeRates() {
        RestTemplate restTemplate = new RestTemplate();
        RateResponse rates = fetchRatesFromApi(restTemplate);
        if (rates != null && "success".equalsIgnoreCase(rates.getResult())) {
            Map<String, Double> conversionRates = rates.getConversion_rates();
            supportedCurrencies.forEach(currency -> {
                double rate = conversionRates.getOrDefault(currency, 1.0); // Если курс не найден, используем 1.0
                String currencyPair = currency + "/USD";
                saveExchangeRate(currencyPair, rate);
            });
        } else {
            // Логируем ошибку или обрабатываем неудачный ответ
            System.out.println("Ошибка при получении обменных курсов: " + (rates != null ? rates.getResult() : "null response"));
        }
    }
    private RateResponse fetchRatesFromApi(RestTemplate restTemplate) {
        try {
            return restTemplate.getForObject(EXCHANGE_RATE_API, RateResponse.class);
        } catch (Exception e) {
            System.err.println("Ошибка при обращении к API: " + e.getMessage());
            return null;
        }
    }
    private RateResponse getLastAvailableExchangeRates() {
        List<ExchangeRate> rates = exchangeRateRepository.findAll(Sort.by(Sort.Direction.DESC, "localDate"));
        if (!rates.isEmpty()) {
            RateResponse rateResponse = new RateResponse();
            rateResponse.setResult("success");
            rateResponse.setBase_code("USD");
            Map<String, Double> conversionRates = new HashMap<>();
            rates.forEach(rate -> {
                String currency = rate.getCurrencyPair().split("/")[0];
                conversionRates.put(currency, rate.getClosingRate());
            });
            rateResponse.setConversion_rates(conversionRates);
            return rateResponse;
        }
        return null;
    }
    private void saveExchangeRate(String pair, double rate) {
        LocalDate localDate = LocalDate.now();
        ExchangeRate exchangeRate = new ExchangeRate(pair, rate, localDate);
        exchangeRateRepository.save(exchangeRate);
    }
    public List<ExchangeRate> getCurrentExchangeRates() {
        return exchangeRateRepository.findAll(Sort.by(Sort.Direction.DESC, "localDate"));
    }
    public double calculateExpenseInUSD(double amount, String currency, LocalDate expenseDate) {
        return amount / getExchangeRateForDate(currency, expenseDate);
    }
    private double getExchangeRateForDate(String currency, LocalDate date) {
        String currencyPair = currency + "/USD";
        return exchangeRateRepository.findTopByCurrencyPairAndLocalDateLessThanEqual(currencyPair, date)
                .map(ExchangeRate::getClosingRate)
                .orElseGet(() -> {
                    return exchangeRateRepository.findTopByCurrencyPairAndLocalDateLessThanEqual(currencyPair, LocalDate.now())
                            .map(ExchangeRate::getClosingRate)
                            .orElseThrow(() -> new IllegalArgumentException("Использовано текущий курс"));
                });
    }
}





