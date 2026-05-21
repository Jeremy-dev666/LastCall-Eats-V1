package com.lastcalleats.marketplace.product.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
public class ListingResponse {

    private Long id;
    private Long merchantId;
    private Long templateId;
    private String templateName;
    private BigDecimal discountPrice;
    private BigDecimal originalPrice;
    private Integer quantity;
    private Integer remainingQuantity;
    private LocalTime pickupStart;
    private LocalTime pickupEnd;
    private LocalDate date;
    private Boolean isAvailable;
    private LocalDateTime createdAt;
}
