package com.exchange.api;

import com.exchange.api.mapper.SupportedCurrenciesMapper;
import com.exchange.generated.api.SupportedCurrenciesApi;
import com.exchange.generated.model.SupportedCurrenciesResponse;
import com.exchange.service.ExchangeRatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class SupportedCurrenciesController implements SupportedCurrenciesApi {
    private final ExchangeRatesService service;
    private final SupportedCurrenciesMapper mapper;

    public SupportedCurrenciesController(@Autowired ExchangeRatesService service, @Autowired SupportedCurrenciesMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    public Mono<ResponseEntity<SupportedCurrenciesResponse>> getAvailableCurrencies(ServerWebExchange exchange) {
        return service.retrieveSupportedCurrencies()
                .map(mapper::from)
                .map(ResponseEntity::ok);
    }
}
