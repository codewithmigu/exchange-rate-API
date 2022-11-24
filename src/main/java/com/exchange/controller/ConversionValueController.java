package com.exchange.controller;

import com.exchange.generated.api.ConversionValueApi;
import com.exchange.generated.api.ConversionValuesApi;
import com.exchange.generated.model.ConversionValueRequest;
import com.exchange.generated.model.ConversionValueResponse;
import com.exchange.generated.model.ConversionValuesRequest;
import com.exchange.generated.model.ConversionValuesResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class ConversionValueController implements ConversionValueApi, ConversionValuesApi {
    @Override
    public Mono<ResponseEntity<ConversionValueResponse>> getConversionValue(Mono<ConversionValueRequest> conversionValueRequest, ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<ConversionValuesResponse>> getConversionValues(Mono<ConversionValuesRequest> conversionValuesRequest, ServerWebExchange exchange) {
        return Mono.empty();
    }
}
