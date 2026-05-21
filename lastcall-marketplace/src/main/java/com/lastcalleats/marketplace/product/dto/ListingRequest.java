package com.lastcalleats.marketplace.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ListingRequest {

    @NotNull
    private Long templateId;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal discountPrice;

    @NotNull
    @Min(1)
    private Integer quantity;

    @NotNull
    private LocalTime pickupStart;

    @NotNull
    private LocalTime pickupEnd;

    @NotNull
    private LocalDate date;
}
