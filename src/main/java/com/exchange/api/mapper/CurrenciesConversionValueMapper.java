package com.exchange.api.mapper;

import com.exchange.generated.model.CurrenciesConversionValueResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class CurrenciesConversionValueMapper {

    private final ConversionValueMapper conversionValueMapper;

    public CurrenciesConversionValueMapper(@Autowired ConversionValueMapper conversionValueMapper) {
        this.conversionValueMapper = conversionValueMapper;
    }

    public CurrenciesConversionValueResponse from(Map<String, BigDecimal> currenciesConversionValueMap) {
        var conversionValues = currenciesConversionValueMap.entrySet()
                .stream()
                .map(e -> conversionValueMapper.from(e.getKey(), e.getValue()))
                .toList();

        return CurrenciesConversionValueResponse.builder()
                .currenciesConversionValue(conversionValues)
                .build();
    }
}
