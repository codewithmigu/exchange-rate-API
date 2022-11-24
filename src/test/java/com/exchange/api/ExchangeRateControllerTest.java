package com.exchange.api;

import com.exchange.generated.model.ExchangeRateResponse;
import com.exchange.generated.model.ExchangeRatesResponse;
import com.exchange.generated.model.ExchangeRatesResponseExchangeRatesInner;
import io.vavr.Tuple2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.exchange.utils.Utils.*;

@WebFluxTest(ExchangeRateController.class)
class ExchangeRateControllerTest extends BaseControllerTest {
    // TODO: Add white box testing: add more granular validation after adopting wiremock or other strategy of mocking response from downstream APIs

    private static final String GET_RATE_PATH_URI = "/rate";
    private static final String GET_RATES_PATH_URI = "/rates";

    @Test
    @DisplayName("Get rate endpoint 200 scenario")
    void testGetRate200Scenario() {
        // given
        final var currencyQueryParam = new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, EUR_CURRENCY);
        final var targetQueryParam = new Tuple2<>(TARGET_QUERY_PARAM_NAME, GBP_CURRENCY);

        // when
        final var rateResponse = super.doGet(GET_RATE_PATH_URI, buildQueryParams(currencyQueryParam, targetQueryParam))
                .expectStatus().isOk()
                .returnResult(ExchangeRateResponse.class)
                .getResponseBody()
                .blockFirst();

        // then
        Assertions.assertThat(rateResponse).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("provideParamsForGetRates200Scenario")
    @DisplayName("Get rates endpoint 200 scenario")
    void testGetRates200Scenario(@AggregateWith(VarargsAggregator.class) Tuple2<String, String>... queryParams) {
        // given
        final var expectedCurrencies = Arrays.stream(queryParams)
                .filter(Objects::nonNull)
                .filter(e -> TARGET_QUERY_PARAM_NAME.equalsIgnoreCase(e._1()))
                .map(Tuple2::_2)
                .collect(Collectors.toUnmodifiableSet());

        // when
        final var ratesResponse = super.doGet(GET_RATES_PATH_URI, buildQueryParams(queryParams))
                .expectStatus().isOk()
                .returnResult(ExchangeRatesResponse.class)
                .getResponseBody()
                .blockFirst();

        // then
        Assertions.assertThat(ratesResponse).isNotNull();
        Assertions.assertThat(ratesResponse.getExchangeRates()).isNotNull();

        final var actualCurrencies = ratesResponse.getExchangeRates()
                .stream()
                .map(ExchangeRatesResponseExchangeRatesInner::getCurrency)
                .collect(Collectors.toUnmodifiableSet());

        Assertions.assertThat(actualCurrencies).isEqualTo(expectedCurrencies);
    }

    @ParameterizedTest
    @MethodSource("provideParamsForGetRate4xxScenario")
    @DisplayName("Get rate endpoint 4xx error scenario")
    void testGetRate4xxResponse(final Tuple2<String, String> currencyQueryParam, final Tuple2<String, String> targetQueryParam, int expectedStatusCode) {
        super.doGet(GET_RATE_PATH_URI, buildQueryParams(currencyQueryParam, targetQueryParam))
                .expectStatus()
                .isEqualTo(expectedStatusCode);
    }

    @ParameterizedTest
    @MethodSource("provideParamsForGetRates4xxScenario")
    @DisplayName("Get rates endpoint 4xx error scenario")
    void testGetRate4xxResponse(int expectedStatusCode, @AggregateWith(VarargsAggregator.class) Tuple2<String, String>... queryParams) {
        super.doGet(GET_RATES_PATH_URI, buildQueryParams(queryParams))
                .expectStatus()
                .isEqualTo(expectedStatusCode);
    }

    @SafeVarargs
    private MultiValueMap<String, String> buildQueryParams(Tuple2<String, String>... queryParamNameAndValue) {
        return Arrays.stream(queryParamNameAndValue)
                .filter(Objects::nonNull)
                .collect(LinkedMultiValueMap::new, (map, e) -> map.add(e._1(), e._2()), (MultiValueMapAdapter::addAll));
    }

    private static Stream<Arguments> provideParamsForGetRates4xxScenario() {
        return Stream.of(
                Arguments.of(
                        400
                ),
                Arguments.of(
                        400,
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, EMPTY)
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
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, EMPTY),
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

    private static Stream<Arguments> provideParamsForGetRates200Scenario() {
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

    private static Stream<Arguments> provideParamsForGetRate4xxScenario() {
        return Stream.of(
//                400 error when at least one mandatory query param is missing or empty, or they don't contain only letters
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, null),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME, GBP_CURRENCY),
                        400),
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, EMPTY),
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
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME, EMPTY),
                        400),
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, GBP_CURRENCY),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME, INVALID_CURRENCY_FORMAT_QUERY_PARAM),
                        400),
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, null),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME
                                , EMPTY),
                        400),
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, EMPTY),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME, null),
                        400),
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, EMPTY),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME
                                , EMPTY),
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