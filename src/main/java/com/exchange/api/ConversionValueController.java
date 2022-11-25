package com.exchange.api;

import com.exchange.api.mapper.ConversionValueMapper;
import com.exchange.api.mapper.CurrenciesConversionValueMapper;
import com.exchange.generated.api.ConversionValueApi;
import com.exchange.generated.model.ConversionValueRequest;
import com.exchange.generated.model.ConversionValueResponse;
import com.exchange.generated.model.CurrenciesConversionValueRequest;
import com.exchange.generated.model.CurrenciesConversionValueResponse;
import com.exchange.service.ExchangeRateCalculator;
import com.exchange.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class ConversionValueController implements ConversionValueApi {

    private final ExchangeRateCalculator service;
    private final ConversionValueMapper conversionValueMapper;
    private final CurrenciesConversionValueMapper currenciesConversionValueMapper;

    public ConversionValueController(@Autowired ExchangeRateCalculator service,
                                     @Autowired ConversionValueMapper conversionValueMapper,
                                     @Autowired CurrenciesConversionValueMapper currenciesConversionValueMapper) {
        this.service = service;
        this.conversionValueMapper = conversionValueMapper;
        this.currenciesConversionValueMapper = currenciesConversionValueMapper;
    }

    @Override
    public Mono<ResponseEntity<ConversionValueResponse>> getCurrencyConversionValue(Mono<ConversionValueRequest> conversionValueRequest, ServerWebExchange exchange) {
        return conversionValueRequest.map(request -> service
                        .getConversionValue(request.getValue(), request.getCurrency(), request.getTarget())
                        .map(conversionValue -> conversionValueMapper.from(request.getCurrency(), conversionValue)))
                .flatMap(conversionValueResponseMono -> conversionValueResponseMono)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<CurrenciesConversionValueResponse>> getMultipleCurrenciesConversionValue(Mono<CurrenciesConversionValueRequest> currenciesConversionValueRequest, ServerWebExchange exchange) {
        return currenciesConversionValueRequest.map(request -> service
                        .getCurrenciesConversionValues(request.getValue(), request.getCurrency(), Utils.from(request.getTarget()))
                        .map(currenciesConversionValueMapper::from))
                .flatMap(conversionValueResponseMono -> conversionValueResponseMono)
                .map(ResponseEntity::ok);
    }
}
