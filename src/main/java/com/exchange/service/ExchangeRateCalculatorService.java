package com.exchange.service;

import com.exchange.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        return provider.getExchangeRates()
                .flatMapIterable(Map::entrySet)
                .filter(e -> targetCurrencies.isEmpty() ? Boolean.TRUE : Utils.equalsOneOf(e, baseCurrency, targetCurrencies))
                .collectMap(Map.Entry::getKey, Map.Entry::getValue)
                .map(filteredMap -> filteredMap.keySet().stream()
                        .map(targetCurrency ->
                                Map.entry(targetCurrency, computeConversionValue(filteredMap, BigDecimal.ONE, baseCurrency, targetCurrency)))
                        .filter(e -> targetCurrencies.contains(e.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                );
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
                .filter(e -> targetCurrencies.isEmpty() ? Boolean.TRUE : Utils.equalsOneOf(e, baseCurrency, targetCurrencies))
                .collectMap(Map.Entry::getKey, Map.Entry::getValue)
                .map(targetCurrenciesMap -> targetCurrenciesMap.keySet().stream()
                        .map(targetCurrency ->
                                Map.entry(targetCurrency, computeConversionValue(targetCurrenciesMap, value, baseCurrency, targetCurrency)))
                        .filter(e -> targetCurrencies.contains(e.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                );
    }

    private BigDecimal computeConversionValue(Map<String, BigDecimal> exchangeRatesMap,
                                              BigDecimal baseCurrencyValue,
                                              String baseCurrency,
                                              String targetCurrency) {
        return baseCurrencyValue
                .multiply(exchangeRatesMap.get(targetCurrency))
                .divide(exchangeRatesMap.get(baseCurrency), maxRoundingPrecision, RoundingMode.HALF_EVEN)
                .stripTrailingZeros();
    }
}
