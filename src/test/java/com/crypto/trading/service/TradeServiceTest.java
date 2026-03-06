package com.crypto.trading.service;

import com.crypto.trading.dto.BestPriceResponse;
import com.crypto.trading.dto.TradeRequest;
import com.crypto.trading.dto.TradeResponse;
import com.crypto.trading.entity.TradeTransaction;
import com.crypto.trading.entity.User;
import com.crypto.trading.exception.TradingBusinessException;
import com.crypto.trading.repository.TradeTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TradeServiceTest {

    public static final String BTCUSDT = "BTCUSDT";
    @Mock
    private TradeTransactionRepository tradeTransactionRepository;

    @Mock
    private AggregatedPriceService aggregatedPriceService;

    @Mock
    private WalletService walletService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TradeService tradeService;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setUserName("testuser");
    }

    @Test
    void testExecuteBuyTradeSuccessfully() {

        TradeRequest request = new TradeRequest();
        request.setUsername("testuser");
        request.setSymbol(BTCUSDT);
        request.setTradeAction("BUY");
        request.setQuantity(new BigDecimal("2"));

        BestPriceResponse price = new BestPriceResponse();
        price.setSymbol(BTCUSDT);
        price.setAskPrice(new BigDecimal("20000"));
        price.setBidPrice(new BigDecimal("19900"));

        when(userService.getUserByUserName("testuser")).thenReturn(user);
        when(aggregatedPriceService.getAggregatedBestPrice(BTCUSDT)).thenReturn(price);

        TradeResponse response = tradeService.tradeCrypto(request);

        assertThat(response.getSymbol()).isEqualTo(BTCUSDT);
        assertThat(response.getTradeAction()).isEqualTo("BUY");
        assertThat(response.getTradePrice()).isEqualByComparingTo("20000");
        assertThat(response.getTotalPrice()).isEqualByComparingTo("40000");

        verify(walletService).executeBuy(
                eq(user),
                eq(BTCUSDT),
                eq(new BigDecimal("2")),
                eq(new BigDecimal("40000"))
        );

        verify(tradeTransactionRepository).save(any(TradeTransaction.class));
    }

    @Test
    void testExecuteSellTradeSuccessfully() {

        TradeRequest request = new TradeRequest();
        request.setUsername("testuser");
        request.setSymbol(BTCUSDT);
        request.setTradeAction("SELL");
        request.setQuantity(new BigDecimal("1"));

        BestPriceResponse price = new BestPriceResponse();
        price.setBidPrice(new BigDecimal("21000"));
        price.setAskPrice(new BigDecimal("21100"));

        when(userService.getUserByUserName("testuser")).thenReturn(user);
        when(aggregatedPriceService.getAggregatedBestPrice(BTCUSDT)).thenReturn(price);

        TradeResponse response = tradeService.tradeCrypto(request);

        assertThat(response.getTradeAction()).isEqualTo("SELL");
        assertThat(response.getTradePrice()).isEqualByComparingTo("21000");

        verify(walletService).executeSell(
                eq(user),
                eq(BTCUSDT),
                eq(new BigDecimal("1")),
                eq(new BigDecimal("21000"))
        );

        verify(tradeTransactionRepository).save(any(TradeTransaction.class));
    }

    @Test
    void testThrowExceptionForInvalidSymbol() {

        TradeRequest request = new TradeRequest();
        request.setUsername("testuser");
        request.setSymbol("DOGEUSDT");
        request.setTradeAction("BUY");
        request.setQuantity(new BigDecimal("1"));

        assertThatThrownBy(() ->
                tradeService.tradeCrypto(request))
                .isInstanceOf(TradingBusinessException.class)
                .hasMessageContaining("Pair crypto is invalid");
    }

    @Test
    void testThrowExceptionForInvalidQuantity() {

        TradeRequest request = new TradeRequest();
        request.setUsername("testuser");
        request.setSymbol(BTCUSDT);
        request.setTradeAction("BUY");
        request.setQuantity(BigDecimal.ZERO);

        assertThatThrownBy(() ->
                tradeService.tradeCrypto(request))
                .isInstanceOf(TradingBusinessException.class)
                .hasMessageContaining("Quantity have to be greater than 0");
    }

    @Test
    void testThrowExceptionForInvalidTradeAction() {

        TradeRequest request = new TradeRequest();
        request.setUsername("testuser");
        request.setSymbol(BTCUSDT);
        request.setTradeAction("HOLD");
        request.setQuantity(new BigDecimal("1"));

        BestPriceResponse price = new BestPriceResponse();
        price.setAskPrice(new BigDecimal("20000"));
        price.setBidPrice(new BigDecimal("19900"));

        when(userService.getUserByUserName("testuser")).thenReturn(user);
        when(aggregatedPriceService.getAggregatedBestPrice(BTCUSDT)).thenReturn(price);

        assertThatThrownBy(() ->
                tradeService.tradeCrypto(request))
                .isInstanceOf(TradingBusinessException.class)
                .hasMessageContaining("Invalid trading action");
    }
}
