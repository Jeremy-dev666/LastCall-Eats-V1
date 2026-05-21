package com.lastcalleats.identity.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class MerchantDashboardResponse {

    private int todayOrderCount;
    private BigDecimal todayRevenue;
    private int activeListingCount;
}
