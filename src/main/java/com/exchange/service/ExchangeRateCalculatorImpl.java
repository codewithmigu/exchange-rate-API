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
public class ExchangeRateCalculatorImpl implements ExchangeRateCalculator {

    private final ExchangeRatesService service;
    private final int maxRoundingPrecision;

    public ExchangeRateCalculatorImpl(@Autowired ExchangeRatesService service, @Value("${app.rounding.max.scale}") Integer maxRoundingScale) {
        this.service = service;
        this.maxRoundingPrecision = maxRoundingScale;
    }

    @Override
    public Mono<BigDecimal> getCurrencyRate(String baseCurrency, String targetCurrency) {
        return getConversionValue(BigDecimal.ONE, baseCurrency, targetCurrency);
    }

    @Override
    public Mono<Map<String, BigDecimal>> getCurrenciesRates(String baseCurrency, Set<String> targetCurrencies) {
        return filterDataAndComputeMap(BigDecimal.ONE, baseCurrency, targetCurrencies);
    }

    @Override
    public Mono<BigDecimal> getConversionValue(BigDecimal value, String baseCurrency, String targetCurrency) {
        return service.retrieveSupportedCurrencies()
                .map(map -> computeConversionValue(map, value, baseCurrency, targetCurrency));
    }

    @Override
    public Mono<Map<String, BigDecimal>> getCurrenciesConversionValues(BigDecimal value, String baseCurrency, Set<String> targetCurrencies) {

        return filterDataAndComputeMap(value, baseCurrency, targetCurrencies);
    }

    /**
     * Retrieves currencies from 3rd party API and filters them with supported currencies by the application - more details {@link ExchangeRatesService}.
     * Their corresponding decimals are retrieved also.
     * A filter is being applied with following logic:
     * - if there are any supplied currencies, then all the other elements are filter out except supplied currencies and base currency (last one needed for conversion computation)
     * - for each remaining element, the conversion value computation result is performed {@link ExchangeRateCalculatorImpl#computeConversionValue}
     * - if there are any supplied currencies, then all the elements except target currencies are filtered out (at this point, only base currency should be the extra element)
     * @param value - given value which is needed to be converted into target currency. When only currency rate is needed, this will be 1.
     * @param baseCurrency - given source / base currency which will be used to determine the decimals values from the map
     * @param targetCurrencies - given target currencies which will be used to determine the decimals values from the map
     * @return {@link Map} containing the target currencies, alongside their decimal value
     */
    private Mono<Map<String, BigDecimal>> filterDataAndComputeMap(BigDecimal value, String baseCurrency, Set<String> targetCurrencies) {
        return service.retrieveSupportedCurrencies()
                .flatMapIterable(Map::entrySet)
                .filter(e -> targetCurrencies.isEmpty() ? Boolean.TRUE : Utils.entryKeyEqualsOneOf(e, baseCurrency, targetCurrencies))
                .collectMap(Map.Entry::getKey, Map.Entry::getValue)
                .map(targetCurrenciesMap -> targetCurrenciesMap.keySet().stream()
                        .map(targetCurrency ->
                                Map.entry(targetCurrency, computeConversionValue(targetCurrenciesMap, value, baseCurrency, targetCurrency)))
                        .filter(e -> targetCurrencies.isEmpty() ? Boolean.TRUE : targetCurrencies.contains(e.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                );
    }

    /**
     * Using the mathematical rule of three which allows us to solve this problem using proportions we can determine the conversion currency value.
     * Based on given parameters, we already know or can determine based from given {@link Map} the following:
     * - source/base currency given value which is required to be converted into target currency
     * - based on source/base and target currency codes, we can retrieve from {@link Map} their decimal values
     * Knowing those 3 parameters and applying the mathematical rule of three we can determine the decimal value of given target currency
     * @param exchangeRatesMap - {@link Map} containing needed currency codes (received from 3rd party and filtered by the app) and their corresponding decimal values
     * @param baseCurrencyValue - given value which is needed to be converted into target currency. When only currency rate is needed, this will be 1.
     * @param baseCurrency - given source / base currency which will be used to determine the decimals values from the map
     * @param targetCurrency - given target currency which will be used to determine the decimals values from the map
     * @return {@link BigDecimal} of given target currency by using mathematical rule of three
     */
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
