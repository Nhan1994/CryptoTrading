package com.crypto.trading.service;

import com.crypto.trading.dto.BestPriceResponse;
import com.crypto.trading.entity.AggregatedPrice;
import com.crypto.trading.exception.TradingBusinessException;
import com.crypto.trading.repository.AggregatedPriceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AggregatedPriceServiceTest {

    public static final String BTCUSDT = "BTCUSDT";
    @Mock
    private AggregatedPriceRepository aggregatedPriceRepository;

    @InjectMocks
    private AggregatedPriceService aggregatedPriceService;

    @Test
    void testReturnBestPriceForBTC() {
        AggregatedPrice price = AggregatedPrice.builder()
                .symbol(BTCUSDT)
                .bestBid(new BigDecimal("20000"))
                .bestAsk(new BigDecimal("20100"))
                .createdAt(LocalDateTime.now())
                .build();

        when(aggregatedPriceRepository
                .findTopBySymbolOrderByCreatedAtDesc(BTCUSDT))
                .thenReturn(Optional.of(price));

        BestPriceResponse response =
                aggregatedPriceService.getAggregatedBestPrice(BTCUSDT);

        assertThat(response.getSymbol()).isEqualTo(BTCUSDT);
        assertThat(response.getBidPrice())
                .isEqualByComparingTo("20000");
        assertThat(response.getAskPrice())
                .isEqualByComparingTo("20100");

        verify(aggregatedPriceRepository)
                .findTopBySymbolOrderByCreatedAtDesc(BTCUSDT);
    }

    @Test
    void testThrowExceptionForInvalidSymbol() {
        assertThatThrownBy(() ->
                aggregatedPriceService.getAggregatedBestPrice("XRPUSDT"))
                .isInstanceOf(TradingBusinessException.class)
                .hasMessageContaining("Invalid symbol input");

        verifyNoInteractions(aggregatedPriceRepository);
    }

    @Test
    void testReturnEmptyResponseWhenPriceNotFound() {
        when(aggregatedPriceRepository
                .findTopBySymbolOrderByCreatedAtDesc(BTCUSDT))
                .thenReturn(Optional.empty());

        BestPriceResponse response =
                aggregatedPriceService.getAggregatedBestPrice(BTCUSDT);

        assertThat(response.getSymbol()).isNull();
        assertThat(response.getBidPrice()).isNull();
        assertThat(response.getAskPrice()).isNull();

        verify(aggregatedPriceRepository)
                .findTopBySymbolOrderByCreatedAtDesc(BTCUSDT);
    }
}
