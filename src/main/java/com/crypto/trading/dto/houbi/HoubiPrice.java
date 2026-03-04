package com.crypto.trading.dto.houbi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HoubiPrice {

    private String symbol;

    private String open;
    private String high;
    private String low;
    private String close;

    private String amount;
    private String vol;
    private Long count;

    private String bid;
    private String bidSize;

    private String ask;
    private String askSize;
}
