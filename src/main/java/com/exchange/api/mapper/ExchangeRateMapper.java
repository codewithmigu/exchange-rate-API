package com.exchange.api.mapper;

import com.exchange.generated.model.ExchangeRateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper
public interface ExchangeRateMapper {
    @Mapping(source = "value", target = "rate")
    ExchangeRateResponse from(String currency, BigDecimal value);
}
