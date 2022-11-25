package com.exchange.service;

import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public interface ExchangeRateCalculator {

    Mono<BigDecimal> getCurrencyRate(String baseCurrency, String targetCurrency);

    Mono<Map<String, BigDecimal>> getCurrenciesRates(String baseCurrency, Set<String> targetCurrencies);

    Mono<BigDecimal> getConversionValue(BigDecimal value, String baseCurrency, String targetCurrency);

    Mono<Map<String, BigDecimal>> getCurrenciesConversionValues(BigDecimal value, String baseCurrency, Set<String> targetCurrencies);
}
