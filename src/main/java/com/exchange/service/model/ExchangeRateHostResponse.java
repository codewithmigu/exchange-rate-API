package com.exchange.service.model;

import java.math.BigDecimal;
import java.util.Map;

public record ExchangeRateHostResponse(
        String base,
        String date,
        Map<String, BigDecimal> rates,
        Boolean success) {
}
