package com.exchange.service;

import com.exchange.config.CacheScheduler;
import com.exchange.service.model.ExchangeRateHostResponse;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

@Service
@CacheConfig(cacheNames = {CacheScheduler.CACHE_NAME})
public class ExchangeRateProviderService {
    private final WebClient webClient;
    @Getter
    private final String baseCurrency;

    public ExchangeRateProviderService(@Autowired WebClient webClient, @Value("${app.base.currency}") String baseCurrency) {
        this.webClient = webClient;
        this.baseCurrency = baseCurrency;
    }

    @Cacheable(CacheScheduler.CACHE_NAME)
    public Mono<Map<String, BigDecimal>> getExchangeRates() {
        return callClient().map(ExchangeRateHostResponse::rates).cache(Duration.ofMinutes(2));
    }

    private Mono<ExchangeRateHostResponse> callClient() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("api.exchangerate.host/latest")
                        .queryParam("base", baseCurrency)
                        .build())
                .retrieve()
                .bodyToMono(ExchangeRateHostResponse.class);
    }
}
