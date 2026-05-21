package com.lastcalleats.common.provider;

import java.math.BigDecimal;

public interface OrderStatsProvider {

    int getTodayOrderCount(Long merchantId);

    BigDecimal getTodayRevenue(Long merchantId);
}
