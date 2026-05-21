package com.lastcalleats.marketplace.order.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class OrderResponse {

    private Long id;
    private Long userId;
    private Long listingId;
    private Long merchantId;
    private String productName;
    private BigDecimal price;
    private String status;
    private String numericCode;
    private String qrCode;
    private LocalDateTime createdAt;
}
