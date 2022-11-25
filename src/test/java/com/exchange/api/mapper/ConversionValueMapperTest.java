package com.exchange.api.mapper;

import com.exchange.generated.model.ConversionValueResponse;
import com.exchange.utils.TestUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ConversionValueMapperTest {

    private final ConversionValueMapper mapper = Mappers.getMapper(ConversionValueMapper.class);

    @Test
    void from() {
        // given
        var expectedCurrency = TestUtils.USD_CURRENCY;
        var expectedValue = TestUtils.VALID_DECIMAL;

        // when
        ConversionValueResponse actual = mapper.from(expectedCurrency, expectedValue);

        // then
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getValue()).isEqualTo(expectedValue);
        Assertions.assertThat(actual.getCurrency()).isEqualTo(expectedCurrency);
    }
}