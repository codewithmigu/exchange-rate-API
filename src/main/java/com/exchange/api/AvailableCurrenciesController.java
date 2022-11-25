package com.exchange.api;

import com.exchange.generated.api.AvailableCurrenciesApi;
import com.exchange.generated.model.AvailableCurrenciesResponse;
import com.exchange.api.mapper.AvailableCurrenciesMapper;
import com.exchange.service.AvailableCurrenciesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
public class AvailableCurrenciesController implements AvailableCurrenciesApi {
    private final AvailableCurrenciesService service;
    private final AvailableCurrenciesMapper mapper;

    public AvailableCurrenciesController(@Autowired AvailableCurrenciesService service, @Autowired AvailableCurrenciesMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    public Mono<ResponseEntity<AvailableCurrenciesResponse>> getAvailableCurrencies(ServerWebExchange exchange) {
        return Mono.justOrEmpty(
                ResponseEntity.of(
                        Optional.ofNullable(
                                mapper.from(
                                        service.getAvailableCurrencies()
                                )
                        )
                )
        );
    }
}
