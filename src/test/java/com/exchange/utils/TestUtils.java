package com.exchange.utils;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.platform.commons.util.Preconditions;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestUtils {
    public static final String EMPTY_STRING = "";
    public static final String CURRENCY_QUERY_PARAM_NAME = "currency";
    public static final String TARGET_QUERY_PARAM_NAME = "target";
    public static final String EUR_CURRENCY = "EUR";
    public static final String GBP_CURRENCY = "GBP";
    public static final String USD_CURRENCY = "USD";
    public static final String INVALID_CURRENCY_BTC = "BTC";
    public static final String INVALID_CURRENCY_ETH = "ETH";
    public static final String INVALID_CURRENCY_FORMAT_QUERY_PARAM = "12EUR34";

    public static final BigDecimal VALID_DECIMAL = BigDecimal.valueOf(12345.666420);

    public static final BigDecimal SMALLER_THAN_ZERO_DECIMAL = BigDecimal.valueOf(-1234.45);

    public static class VarargsAggregator implements ArgumentsAggregator {
        @Override
        public Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context) throws ArgumentsAggregationException {
            Class<?> parameterType = context.getParameter().getType();
            Preconditions.condition(parameterType.isArray(), () -> String.format("%s is not of type array.", parameterType));
            Class<?> componentType = parameterType.getComponentType();

            return IntStream.range(context.getIndex(), accessor.size())
                    .mapToObj(index -> accessor.get(index, componentType))
                    .toArray(size -> (Object[]) Array.newInstance(componentType, size));
        }
    }

    public static <T> Set<String> getCurrencies(List<T> target, Function<T, String> getCurrencyFun) {
        return target.stream()
                .map(getCurrencyFun)
                .collect(Collectors.toUnmodifiableSet());
    }
}
