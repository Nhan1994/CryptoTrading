package com.crypto.trading.scheduler;

import com.crypto.trading.dto.binance.BinanceResponse;
import com.crypto.trading.dto.houbi.HoubiPrice;
import com.crypto.trading.dto.houbi.HoubiResponse;
import com.crypto.trading.entity.AggregatedPrice;
import com.crypto.trading.repository.AggregatedPriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class PriceAggregationSchedulerTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private AggregatedPriceRepository aggregatedPriceRepository;

    @InjectMocks
    private PriceAggregationScheduler scheduler;

    @BeforeEach
    void setup() {
        scheduler = new PriceAggregationScheduler(restTemplate, aggregatedPriceRepository);
        // inject URLs manually since @Value not available
        org.springframework.test.util.ReflectionTestUtils.setField(
                scheduler, "binanceUrl", "http://testbinance.com");
        org.springframework.test.util.ReflectionTestUtils.setField(
                scheduler, "houbiUrl", "http://testhoubi.com");
    }

    @Test
    void testAggregationForBTCBidAndAskPrice() {
        // --- Mock Binance Response ---
        BinanceResponse binance = new BinanceResponse();
        binance.setSymbol("BTCUSDT");
        binance.setBidPrice("72000");
        binance.setAskPrice("72165");

        ResponseEntity<BinanceResponse[]> binanceResponse = new ResponseEntity<>(new BinanceResponse[]{binance}, HttpStatus.OK);
        when(restTemplate.getForEntity(
                "http://testbinance.com",
                BinanceResponse[].class
        )).thenReturn(binanceResponse);

        // --- Mock Houbi Response ---
        HoubiPrice houbiPrice = new HoubiPrice();
        houbiPrice.setSymbol("BTCUSDT");
        houbiPrice.setBid("71000");
        houbiPrice.setAsk("72379");

        HoubiResponse houbi = new HoubiResponse();
        houbi.setStatus("ok");
        houbi.setData(List.of(houbiPrice));

        ResponseEntity<HoubiResponse> houbiResponse = new ResponseEntity<>(houbi, HttpStatus.OK);
        when(restTemplate.getForEntity(
                "http://testhoubi.com",
                HoubiResponse.class
        )).thenReturn(houbiResponse);

        scheduler.fetchAggregatedPrice();
        ArgumentCaptor<AggregatedPrice> captor =
                ArgumentCaptor.forClass(AggregatedPrice.class);

        verify(aggregatedPriceRepository, atLeastOnce())
                .save(captor.capture());

        AggregatedPrice saved = captor.getValue();

        assertThat(saved.getBestBid())
                .isEqualByComparingTo(new BigDecimal("72000"));

        assertThat(saved.getBestAsk())
                .isEqualByComparingTo(new BigDecimal("72165"));
    }

    @Test
    void testAggregationForETHBidAndAskPrice() {
        // --- Mock Binance Response ---
        BinanceResponse binance = new BinanceResponse();
        binance.setSymbol("ETHUSDT");
        binance.setBidPrice("2000");
        binance.setAskPrice("1912");

        ResponseEntity<BinanceResponse[]> binanceResponse = new ResponseEntity<>(new BinanceResponse[]{binance}, HttpStatus.OK);
        when(restTemplate.getForEntity(
                "http://testbinance.com",
                BinanceResponse[].class
        )).thenReturn(binanceResponse);

        // --- Mock Houbi Response ---
        HoubiPrice houbiPrice = new HoubiPrice();
        houbiPrice.setSymbol("ETHUSDT");
        houbiPrice.setBid("1900");
        houbiPrice.setAsk("1967");

        HoubiResponse houbi = new HoubiResponse();
        houbi.setStatus("ok");
        houbi.setData(List.of(houbiPrice));

        ResponseEntity<HoubiResponse> houbiResponse = new ResponseEntity<>(houbi, HttpStatus.OK);
        when(restTemplate.getForEntity(
                "http://testhoubi.com",
                HoubiResponse.class
        )).thenReturn(houbiResponse);

        scheduler.fetchAggregatedPrice();
        ArgumentCaptor<AggregatedPrice> captor =
                ArgumentCaptor.forClass(AggregatedPrice.class);

        verify(aggregatedPriceRepository, atLeastOnce())
                .save(captor.capture());

        AggregatedPrice saved = captor.getValue();

        assertThat(saved.getBestBid())
                .isEqualByComparingTo(new BigDecimal("2000"));

        assertThat(saved.getBestAsk())
                .isEqualByComparingTo(new BigDecimal("1912"));
    }

    @Test
    void testAggregationForBTCButEmptyPrice() {
        ResponseEntity<BinanceResponse[]> binanceResponse = new ResponseEntity<>(new BinanceResponse[]{}, HttpStatus.OK);
        when(restTemplate.getForEntity(
                "http://testbinance.com",
                BinanceResponse[].class
        )).thenReturn(binanceResponse);

        HoubiResponse houbi = new HoubiResponse();
        houbi.setStatus("ok");
        houbi.setData(List.of());

        ResponseEntity<HoubiResponse> houbiResponse = new ResponseEntity<>(houbi, HttpStatus.OK);
        when(restTemplate.getForEntity(
                "http://testhoubi.com",
                HoubiResponse.class
        )).thenReturn(houbiResponse);

        scheduler.fetchAggregatedPrice();
        verify(aggregatedPriceRepository, never()).save(any());
    }
}
