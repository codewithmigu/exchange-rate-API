package com.exchange.api.mapper;

import com.exchange.generated.model.SupportedCurrenciesResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class SupportedCurrenciesMapper {
    public SupportedCurrenciesResponse from(Map<String, BigDecimal> map) {
        var currencies = map.keySet()
                .stream()
                .toList();

        return SupportedCurrenciesResponse.builder()
                .currencies(currencies)
                .build();
    }
}
