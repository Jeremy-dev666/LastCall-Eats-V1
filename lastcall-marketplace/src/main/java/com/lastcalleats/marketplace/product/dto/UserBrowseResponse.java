package com.lastcalleats.marketplace.product.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class UserBrowseResponse {

    private Long listingId;
    private String templateName;
    private String templateDescription;
    private String merchantName;
    private String merchantAddress;
    private BigDecimal originalPrice;
    private BigDecimal discountPrice;
    private Integer remainingQuantity;
    private LocalTime pickupStart;
    private LocalTime pickupEnd;
    private LocalDate date;
}
