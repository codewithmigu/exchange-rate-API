package com.exchange.service;

import com.exchange.service.model.ExchangeRateHostResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class ExchangeRateProvider {
    // TODO: Implement circuit breaker with fall back using a secondary datasource for rates retrieval e.g. https://open.er-api.com/v6/latest/EUR

    private final WebClient webClient;

    public ExchangeRateProvider(@Autowired WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Retrieves exchange rates from external 3rd party service
     */
    public Mono<Map<String, BigDecimal>> getExchangeRates() {
        return doRequest().map(ExchangeRateHostResponse::rates);
    }

    private Mono<ExchangeRateHostResponse> doRequest() {
        return webClient.get()
                .uri("https://api.exchangerate.host/latest")
                .retrieve()
                .bodyToMono(ExchangeRateHostResponse.class);
    }
}
