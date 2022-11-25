package com.exchange.api;

import com.exchange.api.mapper.CurrenciesExchangeRateMapper;
import com.exchange.api.mapper.ExchangeRateMapper;
import com.exchange.generated.api.RateApi;
import com.exchange.generated.model.CurrenciesRateResponse;
import com.exchange.generated.model.ExchangeRateResponse;
import com.exchange.service.ExchangeRateCalculator;
import com.exchange.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class ExchangeRateController implements RateApi {

    private final ExchangeRateCalculator service;
    private final ExchangeRateMapper exchangeRateMapper;
    private final CurrenciesExchangeRateMapper currenciesExchangeRateMapper;

    public ExchangeRateController(@Autowired ExchangeRateCalculator service,
                                  @Autowired ExchangeRateMapper exchangeRateMapper,
                                  @Autowired CurrenciesExchangeRateMapper currenciesExchangeRateMapper) {
        this.service = service;
        this.exchangeRateMapper = exchangeRateMapper;
        this.currenciesExchangeRateMapper = currenciesExchangeRateMapper;
    }

    @Override
    public Mono<ResponseEntity<ExchangeRateResponse>> getCurrencyRate(String currency, String target, ServerWebExchange exchange) {
        return service.getCurrencyRate(currency, target)
                .map(rate -> exchangeRateMapper.from(target, rate))
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<CurrenciesRateResponse>> getMultipleCurrenciesRate(String currency, @RequestParam(required = false) List<String> target, ServerWebExchange exchange) {
        return service.getCurrenciesRates(currency, Utils.from(target))
                .map(currenciesExchangeRateMapper::from)
                .map(ResponseEntity::ok);
    }
}
