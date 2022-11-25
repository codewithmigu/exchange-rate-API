package com.exchange.service;

import com.exchange.config.CacheScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Currency;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = {CacheScheduler.CACHE_NAME})
public class ExchangeRatesService {

    private final ExchangeRateProvider provider;

    public ExchangeRatesService(@Autowired ExchangeRateProvider provider) {
        this.provider = provider;
    }

    /**
     * Retrieves the exchange rates from a 3rd party API and filters out
     * the currency codes which are not present in {@link Currency} class.
     * This filtering is performed in order to not rely on correctness of the data provided by external 3rd party APIs.
     * After this computation, the filtered data is stored in cache.
     * Cache is evicted at every minute via {@link CacheScheduler} class
     * The expiration time of 2 seconds set in this method is for the cache of the Mono
     * Usually the Mono cache expiration time should be greater than the one of {@link Cacheable}
     * Because during the caching of Mono, its value may not be defined yet
     *
     * @return Filtered currency codes map, alongside their exchange rate value
     */
    @Cacheable(CacheScheduler.CACHE_NAME)
    public Mono<Map<String, BigDecimal>> retrieveSupportedCurrencies() {
        var appSupportedCurrenciesSet = getApplicationSupportedCurrencies();

        return provider.getExchangeRates()
                .flatMapIterable(Map::entrySet)
                .filter(e -> appSupportedCurrenciesSet.contains(e.getKey()))
                .collectMap(Map.Entry::getKey, Map.Entry::getValue)
                .cache(Duration.ofMinutes(2));
    }

    /**
     * @return Set of available currency codes in the app system, provided using {@link Currency} class
     */
    private Set<String> getApplicationSupportedCurrencies() {
        return Currency.getAvailableCurrencies()
                .stream()
                .map(Currency::getCurrencyCode)
                .collect(Collectors.toUnmodifiableSet());
    }
}
