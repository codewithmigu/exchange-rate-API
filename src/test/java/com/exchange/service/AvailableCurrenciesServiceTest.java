package com.exchange.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Currency;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class AvailableCurrenciesServiceTest {

    private final AvailableCurrenciesService service = new AvailableCurrenciesService();

    @Test
    void getAvailableCurrencies() {
        // given
        final var expectedCurrencies = Currency.getAvailableCurrencies()
                .stream()
                .map(Currency::getCurrencyCode)
                .collect(Collectors.toList());

        // when
        final var actualCurrencies = service.getAvailableCurrencies();

        // then
        Assertions.assertThat(actualCurrencies).containsAll(expectedCurrencies);
    }
}