package com.exchange.controller;

import com.exchange.generated.model.ExchangeRateResponse;
import io.vavr.Tuple2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;

import java.util.Arrays;
import java.util.stream.Stream;

@WebFluxTest(ExchangeRateController.class)
class ExchangeRateControllerTest extends BaseControllerTest {
    // TODO: Add white box testing: add more granular validation after adopting wiremock or other strategy of mocking response from downstream APIs

    private static final String GET_RATE_PATH_URI = "/rate";
    private static final String GET_RATES_PATH_URI = "/rates";
    private static final String EMPTY = "";
    private static final String CURRENCY_QUERY_PARAM_NAME = "currency";
    private static final String TARGET_QUERY_PARAM_NAME = "target";

    @Test
    @DisplayName("Get rate endpoint 200 scenario")
    void testGetRate200Scenario() {
        // given
        final var currencyQueryParam = new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, "EUR");
        final var targetQueryParam = new Tuple2<>(TARGET_QUERY_PARAM_NAME, "GBP");

        // when
        ExchangeRateResponse rateResponse = super.doGet(GET_RATE_PATH_URI, buildQueryParams(currencyQueryParam, targetQueryParam))
                .expectStatus().isOk()
                .returnResult(ExchangeRateResponse.class)
                .getResponseBody()
                .blockFirst();

        // then
        Assertions.assertThat(rateResponse).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("provideParamsForGetRate4xxScenarios")
    @DisplayName("Get rate endpoint 4xx error scenario")
    void testGetRate4xxResponse(final Tuple2<String, String> currencyQueryParam, final Tuple2<String, String> targetQueryParam, int expectedStatusCode) {
        super.doGet(GET_RATE_PATH_URI, buildQueryParams(currencyQueryParam, targetQueryParam))
                .expectStatus()
                .isEqualTo(expectedStatusCode);
    }

    @SafeVarargs
    private MultiValueMap<String, String> buildQueryParams(Tuple2<String, String>... queryParamNameAndValue) {
        return Arrays.stream(queryParamNameAndValue)
                .collect(LinkedMultiValueMap::new, (map, e) -> map.add(e._1(), e._2()), (MultiValueMapAdapter::addAll));
    }

    private static Stream<Arguments> provideParamsForGetRate4xxScenarios() {
        return Stream.of(
//                400 error when at least one mandatory query param is missing or empty, or they don't contain only letters
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, null),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME
                                , "USD"),
                        400),
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, EMPTY),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME
                                , "USD"),
                        400),
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, "12EUR34"),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME
                                , "USD"),
                        400),
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, "USD"),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME
                                , null),
                        400),
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, "USD"),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME
                                , EMPTY),
                        400),
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, "USD"),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME
                                , "12USD34"),
                        400),
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, null),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME
                                , EMPTY),
                        400),
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, EMPTY),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME
                                , null),
                        400),
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, EMPTY),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME
                                , EMPTY),
                        400),
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, null),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME
                                , null),
                        400),

//                422 error when at least one of the query params contains an unsupported currency
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, "BTC"),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME
                                , "EUR"),
                        422),
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, "EUR"),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME
                                , "BTC"),
                        422),
                Arguments.of(
                        new Tuple2<>(CURRENCY_QUERY_PARAM_NAME, "BTC"),
                        new Tuple2<>(TARGET_QUERY_PARAM_NAME
                                , "ETH"),
                        422)
        );
    }

}