package com.example.trancs.model;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateResponse {
    private String result;
    private String base_code;
    private Map<String, Double> conversion_rates;
}



