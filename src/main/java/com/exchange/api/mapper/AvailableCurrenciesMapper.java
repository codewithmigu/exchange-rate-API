package com.exchange.api.mapper;

import com.exchange.generated.model.AvailableCurrenciesResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface AvailableCurrenciesMapper {

    @Mapping(target = "currencies")
    AvailableCurrenciesResponse from(String dummy, List<String> currencies);

    default AvailableCurrenciesResponse from(List<String> currencies) {
        return from(currencies.get(0), currencies);
    }
}
