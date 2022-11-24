package com.exchange.api;

import com.exchange.generated.api.AvailableCurrenciesApi;
import com.exchange.generated.model.AvailableCurrenciesResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class AvailableCurrenciesController implements AvailableCurrenciesApi {
    @Override
    public Mono<ResponseEntity<AvailableCurrenciesResponse>> getAvailableCurrencies(ServerWebExchange exchange) {
        return Mono.empty();
    }
}
