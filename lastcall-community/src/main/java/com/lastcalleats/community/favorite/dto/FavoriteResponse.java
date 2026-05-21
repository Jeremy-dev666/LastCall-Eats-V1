package com.lastcalleats.community.favorite.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
public class FavoriteResponse {

    private Long listingId;
    private String templateName;
    private String merchantName;
    private BigDecimal discountPrice;
    private BigDecimal originalPrice;
    private LocalTime pickupStart;
    private LocalTime pickupEnd;
    private LocalDate date;
    private LocalDateTime favoritedAt;
}
