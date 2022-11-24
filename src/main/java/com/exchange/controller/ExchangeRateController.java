package com.exchange.controller;

import com.exchange.generated.api.RateApi;
import com.exchange.generated.api.RatesApi;
import com.exchange.generated.model.ExchangeRateResponse;
import com.exchange.generated.model.ExchangeRatesResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class ExchangeRateController implements RateApi, RatesApi {
    @Override
    public Mono<ResponseEntity<ExchangeRateResponse>> getRate(String currency, String target, ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<ExchangeRatesResponse>> getRates(String currency, List<String> target, ServerWebExchange exchange) {
        return Mono.empty();
    }
}
