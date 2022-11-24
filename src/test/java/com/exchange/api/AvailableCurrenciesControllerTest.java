package com.exchange.api;

import com.exchange.generated.model.AvailableCurrenciesResponse;
import com.exchange.generated.model.ExchangeRateResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;

import static org.junit.jupiter.api.Assertions.*;

@WebFluxTest(AvailableCurrenciesController.class)
class AvailableCurrenciesControllerTest extends BaseControllerTest{

    private static final String AVAILABLE_CURRENCIES_PATH_URI = "/available-currencies";

    @Test
    @DisplayName("Retrieve available currencies happy flow")
    void getAvailableCurrencies() {
        final var response = super.doGet(AVAILABLE_CURRENCIES_PATH_URI, null)
                .expectStatus().isOk()
                .returnResult(AvailableCurrenciesResponse.class)
                .getResponseBody()
                .blockFirst();

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getCurrencies()).isNotNull();
        Assertions.assertThat(response.getCurrencies()).isNotEmpty();
    }
}