package com.exchange.service;

import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.List;

@Service
public class AvailableCurrenciesService {
    public List<String> getAvailableCurrencies() {
        return Currency.getAvailableCurrencies()
                .stream()
                .map(Currency::getCurrencyCode)
                .toList();
    }
}
