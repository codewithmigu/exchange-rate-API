package com.exchange.api;

import com.exchange.generated.api.RateApi;
import com.exchange.generated.model.CurrenciesRateResponse;
import com.exchange.generated.model.ExchangeRateResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class ExchangeRateController implements RateApi {
    @Override
    public Mono<ResponseEntity<ExchangeRateResponse>> getCurrencyRate(String currency, String target, ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<CurrenciesRateResponse>> getMultipleCurrenciesRate(String currency, List<String> target, ServerWebExchange exchange) {
        return Mono.empty();
    }
}
