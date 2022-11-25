package com.exchange.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

@Service
public class ExchangeRateCalculatorService implements ExchangeRateCalculator {

    private final ExchangeRateProviderService provider;
    private final int maxRoundingPrecision;

    public ExchangeRateCalculatorService(@Autowired ExchangeRateProviderService provider,
                                         @Value("${app.rounding.max.scale}") Integer maxRoundingScale) {
        this.provider = provider;
        this.maxRoundingPrecision = maxRoundingScale;
    }

    @Override
    public Mono<BigDecimal> getCurrencyRate(String baseCurrency, String targetCurrency) {
        return getConversionValue(BigDecimal.ONE, baseCurrency, targetCurrency);
    }

    @Override
    public Mono<Map<String, BigDecimal>> getCurrenciesRates(String baseCurrency, Set<String> targetCurrencies) {
        // Predicate used to filter only given currencies as optional query param, if empty then it will return all
        Predicate<Map.Entry<String, BigDecimal>> targetCurrenciesPredicate = e ->
                targetCurrencies.isEmpty() ? Boolean.TRUE : targetCurrencies.contains(e.getKey());

        return provider.getExchangeRates()
                .flatMapIterable(Map::entrySet)
                .filter(targetCurrenciesPredicate)
                .flatMap(e -> getCurrencyRate(baseCurrency, e.getKey())
                        .map(value -> Map.entry(e.getKey(), value)))
                .collectMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    @Override
    public Mono<BigDecimal> getConversionValue(BigDecimal value, String baseCurrency, String targetCurrency) {
        return provider.getExchangeRates()
                .map(map -> computeConversionValue(map, value, baseCurrency, targetCurrency));
    }

    @Override
    public Mono<Map<String, BigDecimal>> getCurrenciesConversionValues(BigDecimal value, String baseCurrency, Set<String> targetCurrencies) {
        return provider.getExchangeRates()
                .flatMapIterable(Map::entrySet)
                .filter(e -> targetCurrencies.contains(e.getKey()))
                .flatMap(e -> getConversionValue(e.getValue(), baseCurrency, e.getKey())
                        .map(conversionValue -> Map.entry(e.getKey(), conversionValue)))
                .collectMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    private BigDecimal computeConversionValue(Map<String, BigDecimal> exchangeRatesMap,
                                              BigDecimal value,
                                              String baseCurrency,
                                              String targetCurrency) {
        return value
                .multiply(exchangeRatesMap.get(targetCurrency))
                .divide(exchangeRatesMap.get(baseCurrency), maxRoundingPrecision, RoundingMode.HALF_EVEN)
                .stripTrailingZeros();
    }
}