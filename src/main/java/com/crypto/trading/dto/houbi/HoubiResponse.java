package com.crypto.trading.dto.houbi;

import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
public class HoubiResponse {

    private List<HoubiPrice> data;
    private String status;
    private Instant ts;
}
