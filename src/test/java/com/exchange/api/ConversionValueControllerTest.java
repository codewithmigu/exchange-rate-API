package com.exchange.api;

import com.exchange.api.config.TestConfig;
import com.exchange.generated.model.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.exchange.utils.TestUtils.*;

@WebFluxTest(ConversionValueController.class)
@ContextConfiguration(classes = TestConfig.class)
class ConversionValueControllerTest extends BaseControllerTest {
    // TODO: Add white box testing: add more granular validation after adopting wiremock or other strategy of mocking response from downstream APIs
    // Maybe add a scenario with 0 as value for both endpoints

    private static final String GET_CONVERSION_VALUE_PATH_URI = "/conversion-value/currency";
    private static final String GET_MULTIPLE_CONVERSION_VALUES_PATH_URI = "/conversion-value/currencies";

    @Test
    @DisplayName("Conversion value retrieval endpoint 200 scenario")
    void testConversionValueRetrieval200Scenario() {
        // given
        final var requestBody = ConversionValueRequest.builder()
                .currency(EUR_CURRENCY)
                .value(VALID_DECIMAL)
                .target(USD_CURRENCY)
                .build();

        // when
        final var response = super.doPost(GET_CONVERSION_VALUE_PATH_URI, requestBody)
                .expectStatus().isOk()
                .returnResult(ExchangeRateResponse.class)
                .getResponseBody()
                .blockFirst();

        // then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getRate()).isNotNull();
        Assertions.assertThat(response.getCurrency()).isNotNull();
    }

    @Test
    @DisplayName("Multiple conversion values retrieval endpoint 200 scenario")
    void testMultipleConversionValuesRetrieval200Scenario() {
        // given
        final var expectedCurrencies = Set.of(USD_CURRENCY, GBP_CURRENCY);
        final var requestBody =
                CurrenciesConversionValueRequest.builder()
                        .currency(EUR_CURRENCY)
                        .value(VALID_DECIMAL)
                        .target(expectedCurrencies.stream().toList())
                        .build();

        // when
        final var response = super.doPost(GET_MULTIPLE_CONVERSION_VALUES_PATH_URI, requestBody)
                .expectStatus().isOk()
                .returnResult(CurrenciesConversionValueResponse.class)
                .getResponseBody()
                .blockFirst();

        // then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getCurrenciesConversionValue()).isNotNull();

        final var actualCurrencies = getCurrencies(response.getCurrenciesConversionValue(), ConversionValueResponse::getCurrency);

        Assertions.assertThat(actualCurrencies).isEqualTo(expectedCurrencies);
    }

    @ParameterizedTest
    @MethodSource("provideParamsForConversionValueRetrieval4xxScenario")
    @DisplayName("Conversion value retrieval endpoint 4xx error scenario")
    void testConversionValueRetrieval4xxResponse(final String currency, final BigDecimal value, final String targetCurrency, final int expectedStatusCode) {
        final var requestBody = ConversionValueRequest.builder()
                .currency(currency)
                .value(value)
                .target(targetCurrency)
                .build();

        super.doPost(GET_CONVERSION_VALUE_PATH_URI, requestBody)
                .expectStatus().isEqualTo(expectedStatusCode);
    }

    @ParameterizedTest
    @MethodSource("provideParamsForMultipleConversionValuesRetrieval4xxScenario")
    @DisplayName("Multiple conversion values retrieval endpoint 4xx error scenario")
    void testMultipleConversionValuesRetrieval4xxResponse(final String currency, final BigDecimal value, final List<String> targetCurrencies, final int expectedStatusCode) {
        final var requestBody = CurrenciesConversionValueRequest.builder()
                .currency(currency)
                .value(value)
                .target(targetCurrencies)
                .build();

        super.doPost(GET_MULTIPLE_CONVERSION_VALUES_PATH_URI, requestBody)
                .expectStatus().isEqualTo(expectedStatusCode);
    }

    private static Stream<Arguments> provideParamsForConversionValueRetrieval4xxScenario() {
        return Stream.of(
                Arguments.of(null, VALID_DECIMAL, GBP_CURRENCY, 400),
                Arguments.of(GBP_CURRENCY, VALID_DECIMAL, null, 400),
                Arguments.of(GBP_CURRENCY, null, EUR_CURRENCY, 400),
                Arguments.of(EMPTY_STRING, VALID_DECIMAL, GBP_CURRENCY, 400),
                Arguments.of(EUR_CURRENCY, VALID_DECIMAL, EMPTY_STRING, 400),
                Arguments.of(EUR_CURRENCY, VALID_DECIMAL, INVALID_CURRENCY_BTC, 422),
                Arguments.of(INVALID_CURRENCY_BTC, VALID_DECIMAL, EUR_CURRENCY, 422),
                Arguments.of(GBP_CURRENCY, SMALLER_THAN_ZERO_DECIMAL, EUR_CURRENCY, 422)
        );
    }

    private static Stream<Arguments> provideParamsForMultipleConversionValuesRetrieval4xxScenario() {
        return Stream.of(
                Arguments.of(null, VALID_DECIMAL, List.of(EUR_CURRENCY), 400),
                Arguments.of(EUR_CURRENCY, VALID_DECIMAL, List.of(), 400),
                Arguments.of(USD_CURRENCY, null, List.of(EUR_CURRENCY), 400),
                Arguments.of(EMPTY_STRING, VALID_DECIMAL, List.of(EUR_CURRENCY), 400),
                Arguments.of(EUR_CURRENCY, VALID_DECIMAL, null, 400),
                Arguments.of(EUR_CURRENCY, VALID_DECIMAL, List.of(EMPTY_STRING), 400),
                Arguments.of(INVALID_CURRENCY_BTC, VALID_DECIMAL, List.of(EUR_CURRENCY), 422),
                Arguments.of(GBP_CURRENCY, VALID_DECIMAL, List.of(INVALID_CURRENCY_ETH), 422),
                Arguments.of(GBP_CURRENCY, SMALLER_THAN_ZERO_DECIMAL, List.of(EUR_CURRENCY), 422)
        );
    }
}
