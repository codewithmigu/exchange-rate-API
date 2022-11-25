package com.exchange.api.mapper;

import com.exchange.generated.model.ExchangeRateResponse;
import com.exchange.utils.TestUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ExchangeRateMapperTest {

    private final ExchangeRateMapper mapper = Mappers.getMapper(ExchangeRateMapper.class);

    @Test
    void from() {
        // given
        var expectedCurrency = TestUtils.USD_CURRENCY;
        var expectedValue = TestUtils.VALID_DECIMAL;

        // when
        ExchangeRateResponse actual = mapper.from(expectedCurrency, expectedValue);

        // then
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getRate()).isEqualTo(expectedValue);
        Assertions.assertThat(actual.getCurrency()).isEqualTo(expectedCurrency);
    }
}