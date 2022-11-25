package com.exchange.api.mapper;

import com.exchange.generated.model.ConversionValueResponse;
import org.mapstruct.Mapper;

import java.math.BigDecimal;

@Mapper
public interface ConversionValueMapper {
    ConversionValueResponse from(String currency, BigDecimal value);
}
