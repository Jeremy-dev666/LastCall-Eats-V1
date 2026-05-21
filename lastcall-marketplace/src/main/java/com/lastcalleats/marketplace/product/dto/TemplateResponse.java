package com.lastcalleats.marketplace.product.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class TemplateResponse {

    private Long id;
    private Long merchantId;
    private String name;
    private String description;
    private BigDecimal originalPrice;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
