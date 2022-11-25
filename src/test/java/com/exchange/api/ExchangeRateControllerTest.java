package com.exchange.api;

import com.exchange.api.config.TestConfig;
import com.exchange.generated.model.CurrenciesRateResponse;
import com.exchange.generated.model.ExchangeRateResponse;
import io.vavr.Tuple2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.exchange.utils.TestUtils.*;

@WebFluxTest(ExchangeRateController.class)
@ContextConfiguration(classes = TestConfig.class)
class ExchangeRateControllerTest extends BaseControllerTest {
    // TODO: Add white box testing: add more granular validation after adopting wiremock or other strategy of mocking response from downstream APIs

    private static final String GET_RATE_PATH_URI = "/rate/currency";
    private static final String GET_MULTIPLE_RATES_PATH_URI = "/rate/currencies";

    @Test
    @DisplayName("Exchange rate retrieval endpoint 200 scenario")
    void testExchangeRateRetrieval200Scenario() {
        // given
        final var currencyQueryParam = new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, EUR_CURRENCY);
        final var targetQueryParam = new Tuple2<>(TARGET_QUERY_PARAM_NAME, GBP_CURRENCY);

        // when
        final var response = super.doGet(GET_RATE_PATH_URI, buildQueryParams(currencyQueryParam, targetQueryParam))
                .expectStatus().isOk()
                .returnResult(ExchangeRateResponse.class)
                .getResponseBody()
                .blockFirst();

        // then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getRate()).isNotNull();
        Assertions.assertThat(response.getCurrency()).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("provideParamsForMultipleExchangeRatesRetrieval200Scenario")
    @DisplayName("Multiple exchange rates retrieval  endpoint 200 scenario")
    void testMultipleExchangeRatesRetrieval200Scenario(@AggregateWith(VarargsAggregator.class) final Tuple2<String, String>... queryParams) {
        // given
        final var expectedCurrencies = Arrays.stream(queryParams)
                .filter(Objects::nonNull)
                .filter(e -> TARGET_QUERY_PARAM_NAME.equalsIgnoreCase(e._1()))
                .map(Tuple2::_2)
                .collect(Collectors.toUnmodifiableSet());

        // when
        final var response = super.doGet(GET_MULTIPLE_RATES_PATH_URI, buildQueryParams(queryParams))
                .expectStatus().isOk()
                .returnResult(CurrenciesRateResponse.class)
                .getResponseBody()
                .blockFirst();

        // then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getExchangeRates()).isNotNull();

        final var actualCurrencies = getCurrencies(response.getExchangeRates(),
                ExchangeRateResponse::getCurrency);

        Assertions.assertThat(actualCurrencies).isEqualTo(expectedCurrencies);
    }

    @ParameterizedTest
    @MethodSource("provideParamsForExchangeRateRetrieval4xxScenario")
    @DisplayName("Exchange rate retrieval endpoint 4xx error scenario")
    void testExchangeRateRetrieval4xxResponse(final Tuple2<String, String> currencyQueryParam,
                                              final Tuple2<String, String> targetQueryParam, final int expectedStatusCode) {
        super.doGet(GET_RATE_PATH_URI, buildQueryParams(currencyQueryParam, targetQueryParam))
                .expectStatus()
                .isEqualTo(expectedStatusCode);
    }

    @ParameterizedTest
    @MethodSource("provideParamsForMultipleExchangeRatesRetrieval4xxScenario")
    @DisplayName("Multiple exchange rates retrieval  endpoint 4xx error scenario")
    void testExchangeRateRetrieval4xxResponse(final int expectedStatusCode,
                                              @AggregateWith(VarargsAggregator.class) final Tuple2<String, String>... queryParams) {
        super.doGet(GET_MULTIPLE_RATES_PATH_URI, buildQueryParams(queryParams))
                .expectStatus()
                .isEqualTo(expectedStatusCode);
    }

    @SafeVarargs
    private MultiValueMap<String, String> buildQueryParams(final Tuple2<String, String>... queryParamNameAndValue) {
        return Arrays.stream(queryParamNameAndValue)
                .filter(Objects::nonNull)
                .collect(LinkedMultiValueMap::new, (map, e) -> map.add(e._1(), e._2()), (MultiValueMapAdapter::addAll));
    }

    private static Stream<Arguments> provideParamsForMultipleExchangeRatesRetrieval4xxScenario() {
        return Stream.of(
                Arguments.of(
                        400
                ),
                Arguments.of(
                        400,
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, EMPTY_STRING)
                ),
                Arguments.of(
                        400,
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, null)
                ),
                Arguments.of(
                        400,
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, INVALID_CURRENCY_FORMAT_QUERY_PARAM)
                ),
                Arguments.of(
                        400,
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME, EUR_CURRENCY)
                ),
                Arguments.of(
                        400,
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, EMPTY_STRING),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME, EUR_CURRENCY)
                ),
                Arguments.of(
                        400,
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, null),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME, EUR_CURRENCY)
                ),
                Arguments.of(
                        400,
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, INVALID_CURRENCY_FORMAT_QUERY_PARAM),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME, EUR_CURRENCY)
                ),
                Arguments.of(
                        422,
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, INVALID_CURRENCY_BTC)
                ),
                Arguments.of(
                        422,
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, GBP_CURRENCY),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME, EUR_CURRENCY),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME, INVALID_CURRENCY_BTC)
                ),
                Arguments.of(
                        422,
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, INVALID_CURRENCY_BTC),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME, EUR_CURRENCY)
                )
        );
    }

    private static Stream<Arguments> provideParamsForMultipleExchangeRatesRetrieval200Scenario() {
        return Stream.of(
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, EUR_CURRENCY)
                ),
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, EUR_CURRENCY),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME, GBP_CURRENCY)
                ),
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, EUR_CURRENCY),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME, GBP_CURRENCY),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME, USD_CURRENCY)
                )
        );
    }

    private static Stream<Arguments> provideParamsForExchangeRateRetrieval4xxScenario() {
        return Stream.of(
//                400 error when at least one mandatory query param is missing or empty, or they don't contain only letters
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, null),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME, GBP_CURRENCY),
                        400),
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, EMPTY_STRING),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME, GBP_CURRENCY),
                        400),
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, INVALID_CURRENCY_FORMAT_QUERY_PARAM),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME, GBP_CURRENCY),
                        400),
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, GBP_CURRENCY),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME, null),
                        400),
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, GBP_CURRENCY),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME, EMPTY_STRING),
                        400),
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, GBP_CURRENCY),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME, INVALID_CURRENCY_FORMAT_QUERY_PARAM),
                        400),
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, null),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME
                                , EMPTY_STRING),
                        400),
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, EMPTY_STRING),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME, null),
                        400),
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, EMPTY_STRING),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME
                                , EMPTY_STRING),
                        400),
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, null),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME, null),
                        400),

//                422 error when at least one of the query params contains an unsupported currency
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, INVALID_CURRENCY_BTC),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME, EUR_CURRENCY),
                        422),
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, EUR_CURRENCY),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME, INVALID_CURRENCY_BTC),
                        422),
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, INVALID_CURRENCY_BTC),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME, INVALID_CURRENCY_ETH),
                        422)
        );
    }

}