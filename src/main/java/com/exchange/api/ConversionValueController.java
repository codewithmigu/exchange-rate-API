package com.exchange.api;

import com.exchange.generated.api.ConversionValueApi;
import com.exchange.generated.model.ConversionValueRequest;
import com.exchange.generated.model.ConversionValueResponse;
import com.exchange.generated.model.CurrenciesConversionValueRequest;
import com.exchange.generated.model.CurrenciesConversionValueResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class ConversionValueController implements ConversionValueApi {
    @Override
    public Mono<ResponseEntity<ConversionValueResponse>> getCurrencyConversionValue(Mono<ConversionValueRequest> conversionValueRequest, ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<CurrenciesConversionValueResponse>> getMultipleCurrenciesConversionValue(Mono<CurrenciesConversionValueRequest> currenciesConversionValueRequest, ServerWebExchange exchange) {
        return Mono.empty();
    }
}
