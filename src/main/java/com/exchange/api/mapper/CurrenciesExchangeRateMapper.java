package com.exchange.api.mapper;

import com.exchange.generated.model.CurrenciesRateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class CurrenciesExchangeRateMapper {

    private final ExchangeRateMapper mapper;

    public CurrenciesExchangeRateMapper(@Autowired ExchangeRateMapper mapper) {
        this.mapper = mapper;
    }

    public CurrenciesRateResponse from(Map<String, BigDecimal> currenciesExchangeRateMap) {
        var exchangeRates = currenciesExchangeRateMap.entrySet()
                .stream()
                .map(e -> mapper.from(e.getKey(), e.getValue()))
                .toList();

        return CurrenciesRateResponse.builder()
                .exchangeRates(exchangeRates)
                .build();
    }
}
